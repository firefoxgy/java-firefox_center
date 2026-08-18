package com.firefox.center.sys.core.system.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.constant.CacheConstant;
import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.system.api.ISysBaseAPI;
import com.firefox.center.sys.common.system.util.JwtUtil;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.common.util.*;
import com.firefox.center.sys.common.util.encryption.EncryptedString;
import com.firefox.center.sys.core.shiro.vo.DefContants;
import com.firefox.center.sys.core.system.entity.SysDepart;
import com.firefox.center.sys.core.system.entity.SysUser;
import com.firefox.center.sys.core.system.model.SysLoginModel;
import com.firefox.center.sys.core.system.service.ISysDepartService;
import com.firefox.center.sys.core.system.service.ISysDictService;
import com.firefox.center.sys.core.system.service.ISysLogService;
import com.firefox.center.sys.core.system.service.ISysUserService;
import com.firefox.center.sys.core.system.util.RandImageUtil;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Author scott
 * @since 2018-12-17
 */
@Slf4j
@RestController
@RequestMapping("/sys")
@Api(tags="用户登录")
@RequiredArgsConstructor
public class LoginController {

	private final ISysUserService sysUserService;
	private final ISysBaseAPI sysBaseAPI;
	private final ISysLogService logService;
    private final RedisUtil redisUtil;
    private final ISysDepartService sysDepartService;
    private final ISysDictService sysDictService;

	private static final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

	@ApiOperation("登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public R<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		R<JSONObject> result = new R<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
        String captcha = sysLoginModel.getCaptcha();
        if(captcha==null){
			return R.error("验证码无效");
        }
        String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = MD5Util.getMD5(lowerCaseCaptcha+sysLoginModel.getCheckKey());
		Object checkCode = redisUtil.get(realKey);
		if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
			return R.error("验证码错误");
		}
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		if(sysUser==null){
			return R.error("用户不存在");
		}
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			return R.error("用户名或密码错误");
		}
				
		//用户登录信息
		result=userInfo(sysUser, result);
		sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

		return result;
	}
	
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public R<Object> logout(HttpServletRequest request,HttpServletResponse response) {
		//用户退出逻辑
	    String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
	    if(oConvertUtils.isEmpty(token)) {
	    	return R.error("退出登录失败！");
	    }
	    String username = JwtUtil.getUsername(token);
		LoginUser sysUser = sysBaseAPI.getUserByName(username);
	    if(sysUser!=null) {
	    	sysBaseAPI.addLog("用户名: "+sysUser.getRealname()+",退出成功！", CommonConstant.LOG_TYPE_1, null);
	    	log.info(" 用户名:  "+sysUser.getRealname()+",退出成功！ ");
	    	//清空用户登录Token缓存
	    	redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
	    	//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.Sys.SYS_USERS_CACHE, sysUser.getUsername()));
			//调用shiro的logout
			SecurityUtils.getSubject().logout();
	    	return R.ok("退出登录成功！");
	    }else {
	    	return R.error("Token无效!");
	    }
	}
	
	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("loginfo")
	public R<JSONObject> loginfo() {
		R<JSONObject> result = new R<JSONObject>();
		JSONObject obj = new JSONObject();
		//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		// 获取一天的开始和结束时间
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dayStart = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date dayEnd = calendar.getTime();
		// 获取系统访问记录
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount(dayStart,dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart,dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		obj.put("todayIp", todayIp);
		result.setData(obj);
		result.okMsg("登录成功");
		return result;
	}
	
	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("visitInfo")
	public R<List<Map<String,Object>>> visitInfo() {
		R<List<Map<String,Object>>> result = new R<List<Map<String,Object>>>();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date dayStart = calendar.getTime();
        List<Map<String,Object>> list = logService.findVisitCount(dayStart, dayEnd);
		result.setData(oConvertUtils.toLowerCasePageList(list));
		return result;
	}
	
	
	/**
	 * 登陆成功选择用户当前部门
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
	public R<JSONObject> selectDepart(@RequestBody SysUser user) {
		R<JSONObject> result = new R<JSONObject>();
		String username = user.getUsername();
		if(oConvertUtils.isEmpty(username)) {
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			username = sysUser.getUsername();
		}
		String orgCode= user.getOrgCode();
		this.sysUserService.updateUserDepart(username, orgCode);
		SysUser sysUser = sysUserService.getUserByName(username);
		JSONObject obj = new JSONObject();
		obj.put("userInfo", sysUser);
		result.setData(obj);
		return result;
	}

	/**
	 * 短信登录接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/sms")
	public R<String> sms(@RequestBody JSONObject jsonObject) {
		R<String> result = new R<String>();
		String mobile = jsonObject.get("mobile").toString();
		//手机号模式 登录模式: "2"  注册模式: "1"
		String smsmode=jsonObject.get("smsmode").toString();
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			result.setMsg("手机号不允许为空！");
			result.setSuccess(false);
			return result;
		}
		Object object = redisUtil.get(mobile);
		if (object != null) {
			result.setMsg("验证码10分钟内，仍然有效！");
			result.setSuccess(false);
			return result;
		}

		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
    	obj.put("code", captcha);
		try {
			boolean b = false;
			//注册模板
			if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				if(sysUser!=null) {
					result.setSuccess(false);
					result.errorMsg(" 手机号已经注册，请直接登录！");
					sysBaseAPI.addLog("手机号已经注册，请直接登录！", CommonConstant.LOG_TYPE_1, null);
					return result;
				}
				b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
			}else {
				//登录模式，校验用户有效性
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				result = sysUserService.checkUserIsEffective(sysUser);
				if(!result.isSuccess()) {
					String message = result.getMsg();
					if("该用户不存在，请注册".equals(message)){
						result.setSuccess(false);
						result.errorMsg("该用户不存在或未绑定手机号");
					}
					return result;
				}
				
				/**
				 * smsmode 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
				 */
				if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
					//登录模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
				} else if(CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
					//忘记密码模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
				}
			}

			if (b == false) {
				result.setMsg("短信验证码发送失败,请稍后重试");
				result.setSuccess(false);
				return result;
			}
			//验证码10分钟内有效
			redisUtil.set(mobile, captcha, 600);
			//update-begin--Author:scott  Date:20190812 for：issues#391
			//result.setData(captcha);
			//update-end--Author:scott  Date:20190812 for：issues#391
			result.setSuccess(true);

		} catch (ClientException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.errorMsg(" 短信接口未配置，请联系管理员！");
			return result;
		}
		return result;
	}
	

	/**
	 * 手机号登录接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("手机号登录接口")
	@PostMapping("/phoneLogin")
	public R<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
		R<JSONObject> result = new R<JSONObject>();
		String phone = jsonObject.getString("mobile");
		
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		String smscode = jsonObject.getString("captcha");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMsg("手机验证码错误");
			return result;
		}
		//用户信息
		userInfo(sysUser, result);
		//添加日志
		sysBaseAPI.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

		return result;
	}


	/**
	 * 用户信息
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private R<JSONObject> userInfo(SysUser sysUser, R<JSONObject> result) {
		String syspassword = sysUser.getPassword();
		String username = sysUser.getUsername();
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
        // 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);

		// 获取用户部门信息
		JSONObject obj = new JSONObject();
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		obj.put("departs", departs);
		if (departs == null || departs.size() == 0) {
			obj.put("multi_depart", 0);
		} else if (departs.size() == 1) {
			sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
			obj.put("multi_depart", 1);
		} else {
			obj.put("multi_depart", 2);
		}
		obj.put("token", token);
		obj.put("userInfo", sysUser);
		obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
		result.setData(obj);
		result.okMsg("登录成功");
		return result;
	}

	/**
	 * 获取加密字符串
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public R<Map<String,String>> getEncryptedString(){
		R<Map<String,String>> result = new R<Map<String,String>>();
		Map<String,String> map = new HashMap<String,String>();
		map.put("key", EncryptedString.key);
		map.put("iv",EncryptedString.iv);
		result.setData(map);
		return result;
	}

	/**
	 * 后台生成图形验证码 ：有效
	 * @param response
	 * @param key
	 */
	@ApiOperation("获取验证码")
	@GetMapping(value = "/randomImage/{key}", produces = "image/png")
	public void randomImage(HttpServletResponse response,@PathVariable String key) throws IOException {
		if (StringUtils.equalsIgnoreCase("arithmetic", "gif")) {
			response.setContentType(MediaType.IMAGE_GIF_VALUE);
		} else {
			response.setContentType(MediaType.IMAGE_PNG_VALUE);
		}
		response.setHeader(HttpHeaders.PRAGMA, "No-cache");
		response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
		response.setDateHeader(HttpHeaders.EXPIRES, 0L);
		Captcha captcha = RandImageUtil.createCaptcha("arithmetic");
		String lowerCaseCode = StringUtils.lowerCase(captcha.text());
		while(lowerCaseCode.indexOf("-")!=-1){
			captcha = RandImageUtil.createCaptcha("arithmetic");
			lowerCaseCode = StringUtils.lowerCase(captcha.text());
		}
		String realKey = MD5Util.getMD5(lowerCaseCode+key);
		redisUtil.set(realKey, lowerCaseCode, 60);
		captcha.out(response.getOutputStream());
	}
	
	/**
	 * app登录
	 * @param sysLoginModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	public R<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
		R<JSONObject> result = new R<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		
		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.setSuccess(false);
			result.errorMsg("用户名或密码错误");
			return result;
		}
		
		String orgCode = sysUser.getOrgCode();
		if(oConvertUtils.isEmpty(orgCode)) {
			//如果当前用户无选择部门 查看部门关联信息
			List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
			if (departs == null || departs.size() == 0) {
				result.setSuccess(false);
				result.errorMsg("用户暂未归属部门,不可登录!");
				return result;
			}
			orgCode = departs.get(0).getOrgCode();
			sysUser.setOrgCode(orgCode);
			this.sysUserService.updateUserDepart(username, orgCode);
		}
		JSONObject obj = new JSONObject();
		//用户登录信息
		obj.put("userInfo", sysUser);
		
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置超时时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
		//token 信息
		obj.put("token", token);
		result.setData(obj);
		result.setSuccess(true);
		result.setCode(200);
		sysBaseAPI.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 图形验证码
	 * @param sysLoginModel
	 * @return
	 */
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public R<?> checkCaptcha(@RequestBody SysLoginModel sysLoginModel){
		String captcha = sysLoginModel.getCaptcha();
		String checkKey = sysLoginModel.getCheckKey();
		if(captcha==null){
			return R.error("验证码无效");
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = MD5Util.getMD5(lowerCaseCaptcha+checkKey);
		Object checkCode = redisUtil.get(realKey);
		if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
			return R.error("验证码错误");
		}
		return R.ok();
	}

}