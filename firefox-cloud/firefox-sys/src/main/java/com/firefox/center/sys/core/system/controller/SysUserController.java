package com.firefox.center.sys.core.system.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.aspect.annotation.PermissionData;
import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.system.api.ISysBaseAPI;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.common.system.util.JwtUtil;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.common.util.ImportExcelUtil;
import com.firefox.center.sys.common.util.PasswordUtil;
import com.firefox.center.sys.common.util.RedisUtil;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.Consts;
import com.firefox.center.sys.core.base.controller.BaseController;
import com.firefox.center.sys.core.system.entity.*;
import com.firefox.center.sys.core.system.model.DepartIdModel;
import com.firefox.center.sys.core.system.model.SysUserSysDepartModel;
import com.firefox.center.sys.core.system.service.*;
import com.firefox.center.sys.core.system.vo.SysDepartUsersVO;
import com.firefox.center.sys.core.system.vo.SysUserRoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

	private final ISysBaseAPI sysBaseAPI;
	private final ISysUserService sysUserService;
    private final ISysConfigService iSysConfigService;
    private final ISysDepartService sysDepartService;
	private final ISysUserRoleService sysUserRoleService;
	private final ISysUserDepartService sysUserDepartService;
	private final ISysUserRoleService userRoleService;
    private final ISysDepartRoleUserService departRoleUserService;
    private final ISysDepartRoleService departRoleService;
	private final RedisUtil redisUtil;

    @Value("${firefox.upload.path}")
    private String upLoadPath;

    /**
     * 获取用户列表数据
     * @param user
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "system/UserList")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public R<IPage<SysUser>> queryPageList(SysUser user,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		R<IPage<SysUser>> result = new R<IPage<SysUser>>();
		QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
    	//TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.ne("username","_reserve_user_external");
        if(!getUser().getUsername().equals(Consts.User.SUPER)){
            queryWrapper.ne("id", Consts.User.SUPER_ID);
        }
		Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
		IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);

		//批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            pageList.getRecords().forEach(item->{
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
		result.setSuccess(true);
		result.setData(pageList);
		log.info(pageList.toString());
		return result;
	}

    @RequestMapping(value = "/getDefaultPwd", method = RequestMethod.GET)
    public R<?> getDefaultPwd() {
        return R.ok(iSysConfigService.findByCode(Consts.Config.BASE_DEFAULT_PASSWORD));
    }

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public R<SysUser> add(@RequestBody JSONObject jsonObject) {
		R<SysUser> result = new R<SysUser>();
		String selectedRoles = jsonObject.getString("selectedroles");
		String selectedDeparts = jsonObject.getString("selecteddeparts");
		try {
			SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
			user.setCreateTime(new Date());//设置创建时间
			String salt = oConvertUtils.randomGen(8);
			user.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
			user.setPassword(passwordEncode);
			user.setStatus(1);
			user.setDelFlag(CommonConstant.DEL_FLAG_0);
			sysUserService.addUserWithRole(user, selectedRoles);
            sysUserService.addUserWithDepart(user, selectedDeparts);
			result.okMsg("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.errorMsg("操作失败");
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public R<SysUser> edit(@RequestBody JSONObject jsonObject) {
		R<SysUser> result = new R<SysUser>();
		try {
			SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
			sysBaseAPI.addLog("编辑用户，id： " +jsonObject.getString("id") ,CommonConstant.LOG_TYPE_2, 2);
			if(sysUser==null) {
				result.errorMsg("未找到对应实体");
			}else {
				SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
				user.setUpdateTime(new Date());
				//String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
				user.setPassword(sysUser.getPassword());
				String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
				sysUserService.editUserWithRole(user, roles);
                sysUserService.editUserWithDepart(user, departs);
                sysUserService.updateNullPhoneEmail();
				result.okMsg("修改成功!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.errorMsg("操作失败");
		}
		return result;
	}

	/**
	 * 删除用户
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public R<?> delete(@RequestParam(name="id",required=true) String id) {
		sysBaseAPI.addLog("删除用户，id： " +id ,CommonConstant.LOG_TYPE_2, 3);
		this.sysUserService.deleteUser(id);
		return R.ok("删除用户成功");
	}

	/**
	 * 批量删除用户
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public R<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		sysBaseAPI.addLog("批量删除用户， ids： " +ids ,CommonConstant.LOG_TYPE_2, 3);
		this.sysUserService.deleteBatchUsers(ids);
		return R.ok("批量删除用户成功");
	}

	/**
	  * 冻结&解冻用户
	 * @param jsonObject
	 * @return
	 */
	@RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
	public R<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
		R<SysUser> result = new R<SysUser>();
		try {
			String ids = jsonObject.getString("ids");
			String status = jsonObject.getString("status");
			String[] arr = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
							new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId,id));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.errorMsg("操作失败"+e.getMessage());
		}
		result.okMsg("操作成功!");
		return result;

    }

    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public R<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        R<SysUser> result = new R<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.errorMsg("未找到对应实体");
        } else {
            result.setData(sysUser);
            result.setSuccess(true);
        }
        return result;
    }

    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public R<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        R<List<String>> result = new R<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.errorMsg("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setData(list);
        }
        return result;
    }


    /**
	  *  校验用户账号是否唯一<br>
	  *  可以校验其他 需要检验什么就传什么。。。
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public R<Boolean> checkOnlyUser(SysUser sysUser) {
        R<Boolean> result = new R<>();
        //如果此参数为false则程序发生异常
        result.setData(true);
        try {
            //通过传入信息查询新的用户信息
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMsg("用户账号已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMsg(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public R<?> changePassword(@RequestBody SysUser sysUser) {
        SysUser u = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (u == null) {
            return R.error("用户不存在！");
        }
        sysUser.setId(u.getId());
        return sysUserService.changePassword(sysUser);
    }

    /**
     * 查询指定用户和部门关联的数据
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public R<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        R<List<DepartIdModel>> result = new R<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMsg("查找成功");
                result.setData(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMsg("查找失败");
            }
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMsg("查找过程中出现了异常: " + e.getMessage());
            return result;
        }

    }

    /**
     * 生成在添加用户情况下没有主键的问题,返回给前端,根据该id绑定部门数据
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public R<String> generateUserId() {
        R<String> result = new R<>();
        System.out.println("我执行了,生成用户ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setData(userId);
        return result;
    }

    /**
     * 根据部门id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public R<List<SysUser>> queryUserByDepId(@RequestParam(name = "id", required = true) String id,@RequestParam(name="realname",required=false) String realname) {
        R<List<SysUser>> result = new R<>();
        //List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        SysDepart sysDepart = sysDepartService.getById(id);
        List<SysUser> userList = sysUserDepartService.queryUserByDepCode(sysDepart.getOrgCode(),realname);

        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            userList.forEach(item->{
                //TODO 临时借用这个字段用于页面展示
                item.setOrgCode(useDepNames.get(item.getId()));
            });
        }

        try {
            result.setSuccess(true);
            result.setData(userList);
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sysUser
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser,HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据--------------------
        String selections = request.getParameter("selections");
       if(!oConvertUtils.isEmpty(selections)){
           queryWrapper.in("id",selections.split(","));
       }
        //update-end--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据----------------------
        List<SysUser> pageList = sysUserService.list(queryWrapper);

        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("用户列表数据", "导出人:"+user.getRealname(), "导出信息");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public R<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (int i = 0; i < listSysUsers.size(); i++) {
                    SysUser sysUserExcel = listSysUsers.get(i);
                    if (StringUtils.isBlank(sysUserExcel.getPassword())) {
                        // 密码默认为 “123456”
                        sysUserExcel.setPassword("123456");
                    }
                    // 密码加密加盐
                    String salt = oConvertUtils.randomGen(8);
                    sysUserExcel.setSalt(salt);
                    String passwordEncode = PasswordUtil.encrypt(sysUserExcel.getUsername(), sysUserExcel.getPassword(), salt);
                    sysUserExcel.setPassword(passwordEncode);
                    try {
                        sysUserService.save(sysUserExcel);
                        successLines++;
                    } catch (Exception e) {
                        errorLines++;
                        String message = e.getMessage();
                        int lineNumber = i + 1;
                        // 通过索引名判断出错信息
                        if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_USERNAME)) {
                            errorMessage.add("第 " + lineNumber + " 行：用户名已经存在，忽略导入。");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_WORK_NO)) {
                            errorMessage.add("第 " + lineNumber + " 行：工号已经存在，忽略导入。");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_PHONE)) {
                            errorMessage.add("第 " + lineNumber + " 行：手机号已经存在，忽略导入。");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_EMAIL)) {
                            errorMessage.add("第 " + lineNumber + " 行：电子邮件已经存在，忽略导入。");
                        } else {
                            errorMessage.add("第 " + lineNumber + " 行：未知错误，忽略导入");
                            log.error(e.getMessage(), e);
                        }
                    }
                    // 批量将部门和用户信息建立关联关系
                    String departIds = sysUserExcel.getDepartIds();
                    if (StringUtils.isNotBlank(departIds)) {
                        String userId = sysUserExcel.getId();
                        String[] departIdArray = departIds.split(",");
                        List<SysUserDepart> userDepartList = new ArrayList<>(departIdArray.length);
                        for (String departId : departIdArray) {
                            userDepartList.add(new SysUserDepart(userId, departId));
                        }
                        sysUserDepartService.saveBatch(userDepartList);
                    }

                }
            } catch (Exception e) {
                errorMessage.add("发生异常：" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                	log.error(e.getMessage(), e);
                }
            }
        }
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
    }

    /**
	 * @功能：根据id 批量查询
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
	public R<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
		R<Collection<SysUser>> result = new R<>();
		String[] userId = userIds.split(",");
		Collection<String> idList = Arrays.asList(userId);
		Collection<SysUser> userRole = sysUserService.listByIds(idList);
		result.setSuccess(true);
		result.setData(userRole);
		return result;
	}

	/**
	 * 首页用户重置密码
	 */
	@RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
	public R<?> changPassword(@RequestBody JSONObject json) {
		String username = json.getString("username");
		String oldpassword = json.getString("oldpassword");
		String password = json.getString("password");
		String confirmpassword = json.getString("confirmpassword");
		SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
		if(user==null) {
			return R.error("用户不存在！");
		}
		return sysUserService.resetPassword(username,oldpassword,password,confirmpassword);
	}

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public R<IPage<SysUser>> userRoleList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        R<IPage<SysUser>> result = new R<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page,roleId,username);
        result.setSuccess(true);
        result.setData(pageList);
        return result;
    }

    /**
     * 给指定角色添加用户
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public R<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        R<String> result = new R<String>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for(String sysUserId:sysUserRoleVO.getUserIdList()) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId,sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id",sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if(one==null){
                    sysUserRoleService.save(sysUserRole);
                }

            }
            result.setMsg("添加成功!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMsg("出错了: " + e.getMessage());
            return result;
        }
    }
    /**
     *   删除指定角色的用户关系
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public R<SysUserRole> deleteUserRole(@RequestParam(name="roleId") String roleId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        R<SysUserRole> result = new R<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id",userId);
            sysUserRoleService.remove(queryWrapper);
            result.okMsg("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.errorMsg("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public R<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name="roleId") String roleId,
            @RequestParam(name="userIds",required=true) String userIds) {
        R<SysUserRole> result = new R<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.okMsg("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.errorMsg("删除失败！");
        }
        return result;
    }

    /**
     * 部门用户列表
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public R<IPage<SysUser>> departUserList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        R<IPage<SysUser>> result = new R<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        //根据部门ID查询,当前和下级所有的部门IDS
        List<String> subDepids = new ArrayList<>();
        //部门id为空时，查询我的部门下所有用户
        if(oConvertUtils.isEmpty(depId)){
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            int userIdentity = user.getUserIdentity() != null?user.getUserIdentity():CommonConstant.USER_IDENTITY_1;
            if(oConvertUtils.isNotEmpty(userIdentity) && userIdentity == CommonConstant.USER_IDENTITY_2 ){
                subDepids = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
            }
        }else{
            subDepids = sysDepartService.getSubDepIdsByDepId(depId);
        }
        if(subDepids != null && subDepids.size()>0){
            IPage<SysUser> pageList = sysUserService.getUserByDepIds(page,subDepids,username);
            //批量查询用户的所属部门
            //step.1 先拿到全部的 useids
            //step.2 通过 useids，一次性查询用户的所属部门名字
            List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
            if(userIds!=null && userIds.size()>0){
                Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
                pageList.getRecords().forEach(item -> {
                    //批量查询用户的所属部门
                    item.setOrgCode(useDepNames.get(item.getId()));
                });
            }
            result.setSuccess(true);
            result.setData(pageList);
        }else{
            result.setSuccess(true);
            result.setData(null);
        }
        return result;
    }


    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 若某个用户包含多个部门，则会显示多条记录，可自行处理成单条记录
     */
    @GetMapping("/queryByOrgCode")
    public R<?> queryByDepartId(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode") String orgCode,
            SysUser userParams
    ) {
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, new Page(pageNo, pageSize));
        return R.ok(pageList);
    }

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 针对通讯录模块做的接口，将多个部门的用户合并成一条记录，并转成对前端友好的格式
     */
    @GetMapping("/queryByOrgCodeForAddressList")
    public R<?> queryByOrgCodeForAddressList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode",required = false) String orgCode,
            SysUser userParams
    ) {
        IPage page = new Page(pageNo, pageSize);
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, page);
        List<SysUserSysDepartModel> list = pageList.getRecords();

        // 记录所有出现过的 user, key = userId
        Map<String, JSONObject> hasUser = new HashMap<>(list.size());

        JSONArray resultJson = new JSONArray(list.size());

        for (SysUserSysDepartModel item : list) {
            String userId = item.getId();
            // userId
            JSONObject getModel = hasUser.get(userId);
            // 之前已存在过该用户，直接合并数据
            if (getModel != null) {
                String departName = getModel.get("departName").toString();
                getModel.put("departName", (departName + " | " + item.getDepartName()));
            } else {
                // 将用户对象转换为json格式，并将部门信息合并到 json 中
                JSONObject json = JSON.parseObject(JSON.toJSONString(item));
                json.remove("id");
                json.put("userId", userId);
                json.put("departId", item.getDepartId());
                json.put("departName", item.getDepartName());
//                json.put("avatar", item.getSysUser().getAvatar());
                resultJson.add(json);
                hasUser.put(userId, json);
            }
        }

        IPage<JSONObject> result = new Page<>(pageNo, pageSize, pageList.getTotal());
        result.setRecords(resultJson.toJavaList(JSONObject.class));
        return R.ok(result);
    }

    /**
     * 给指定部门添加对应的用户
     */
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public R<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        R<String> result = new R<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for(String sysUserId:sysDepartUsersVO.getUserIdList()) {
                SysUserDepart sysUserDepart = new SysUserDepart(null,sysUserId,sysDepId);
                QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id",sysUserId);
                SysUserDepart one = sysUserDepartService.getOne(queryWrapper);
                if(one==null){
                    sysUserDepartService.save(sysUserDepart);
                }
            }
            result.setMsg("添加成功!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMsg("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     *   删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public R<SysUserDepart> deleteUserInDepart(@RequestParam(name="depId") String depId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        R<SysUserDepart> result = new R<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id",userId);
            boolean b = sysUserDepartService.remove(queryWrapper);
            if(b){
                List<SysDepartRole> sysDepartRoleList = departRoleService.list(new QueryWrapper<SysDepartRole>().eq("depart_id",depId));
                List<String> roleIds = sysDepartRoleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
                if(roleIds != null && roleIds.size()>0){
                    QueryWrapper<SysDepartRoleUser> query = new QueryWrapper<>();
                    query.eq("user_id",userId).in("drole_id",roleIds);
                    departRoleUserService.remove(query);
                }
                result.okMsg("删除成功!");
            }else{
                result.errorMsg("当前选中部门与用户无关联关系!");
            }
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.errorMsg("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public R<SysUserDepart> deleteUserInDepartBatch(
            @RequestParam(name="depId") String depId,
            @RequestParam(name="userIds",required=true) String userIds) {
        R<SysUserDepart> result = new R<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id",Arrays.asList(userIds.split(",")));
            boolean b = sysUserDepartService.remove(queryWrapper);
            if(b){
                departRoleUserService.removeDeptRoleUser(Arrays.asList(userIds.split(",")),depId);
            }
            result.okMsg("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.errorMsg("删除失败！");
        }
        return result;
    }
    
    /**
         *  查询当前用户的所有部门/当前部门编码
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public R<Map<String,Object>> getCurrentUserDeparts() {
        R<Map<String,Object>> result = new R<Map<String,Object>>();
        try {
        	LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
            List<SysDepart> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setData(map);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.errorMsg("查询失败！");
        }
        return result;
    }

    


	/**
	 * 用户注册接口
	 * 
	 * @param jsonObject
	 * @param user
	 * @return
	 */
	@PostMapping("/register")
	public R<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUser user) {
		R<JSONObject> result = new R<JSONObject>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");
		Object code = redisUtil.get(phone);
		String username = jsonObject.getString("username");
		//未设置用户名，则用手机号作为用户名
		if(oConvertUtils.isEmpty(username)){
            username = phone;
        }
        //未设置密码，则随机生成一个密码
		String password = jsonObject.getString("password");
		if(oConvertUtils.isEmpty(password)){
            password = RandomUtil.randomString(8);
        }
		String email = jsonObject.getString("email");
		SysUser sysUser1 = sysUserService.getUserByName(username);
		if (sysUser1 != null) {
			result.setMsg("用户名已注册");
			result.setSuccess(false);
			return result;
		}
		SysUser sysUser2 = sysUserService.getUserByPhone(phone);
		if (sysUser2 != null) {
			result.setMsg("该手机号已注册");
			result.setSuccess(false);
			return result;
		}

		if(oConvertUtils.isNotEmpty(email)){
            SysUser sysUser3 = sysUserService.getUserByEmail(email);
            if (sysUser3 != null) {
                result.setMsg("邮箱已被注册");
                result.setSuccess(false);
                return result;
            }
        }

		if (!smscode.equals(code)) {
			result.setMsg("手机验证码错误");
			result.setSuccess(false);
			return result;
		}

		try {
			user.setCreateTime(new Date());// 设置创建时间
			String salt = oConvertUtils.randomGen(8);
			String passwordEncode = PasswordUtil.encrypt(username, password, salt);
			user.setSalt(salt);
			user.setUsername(username);
			user.setRealname(username);
			user.setPassword(passwordEncode);
			user.setEmail(email);
			user.setPhone(phone);
			user.setStatus(CommonConstant.USER_UNFREEZE);
			user.setDelFlag(CommonConstant.DEL_FLAG_0);
			user.setActivitiSync(CommonConstant.ACT_SYNC_0);
			sysUserService.addUserWithRole(user,"ee8626f80f7c2619917b6236f3a7f02b");//默认临时角色 test
			result.okMsg("注册成功");
		} catch (Exception e) {
			result.errorMsg("注册失败");
		}
		return result;
	}

	/**
	 * 根据用户名或手机号查询用户信息
	 * @param
	 * @return
	 */
	@GetMapping("/querySysUser")
	public R<Map<String, Object>> querySysUser(SysUser sysUser) {
		String phone = sysUser.getPhone();
		String username = sysUser.getUsername();
		R<Map<String, Object>> result = new R<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (oConvertUtils.isNotEmpty(phone)) {
			SysUser user = sysUserService.getUserByPhone(phone);
			if(user!=null) {
				map.put("username",user.getUsername());
				map.put("phone",user.getPhone());
				result.setSuccess(true);
				result.setData(map);
				return result;
			}
		}
		if (oConvertUtils.isNotEmpty(username)) {
			SysUser user = sysUserService.getUserByName(username);
			if(user!=null) {
				map.put("username",user.getUsername());
				map.put("phone",user.getPhone());
				result.setSuccess(true);
				result.setData(map);
				return result;
			}
		}
		result.setSuccess(false);
		result.setMsg("验证失败");
		return result;
	}
	
	/**
	 * 用户手机号验证
	 */
	@PostMapping("/phoneVerification")
	public R<Map<String,String>> phoneVerification(@RequestBody JSONObject jsonObject) {
		R<Map<String,String>> result = new R<Map<String,String>>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMsg("手机验证码错误");
			result.setSuccess(false);
			return result;
		}
		//设置有效时间
		redisUtil.set(phone, smscode,600);
		//新增查询用户名
		LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getPhone,phone);
        SysUser user = sysUserService.getOne(query);
        Map<String,String> map = new HashMap<>();
        map.put("smscode",smscode);
        map.put("username",user.getUsername());
        result.setData(map);
		result.setSuccess(true);
		return result;
	}
	
	/**
	 * 用户更改密码
	 */
	@GetMapping("/passwordChange")
	public R<SysUser> passwordChange(@RequestParam(name="username")String username,
										  @RequestParam(name="password")String password,
			                              @RequestParam(name="smscode")String smscode,
			                              @RequestParam(name="phone") String phone) {
        R<SysUser> result = new R<SysUser>();
        if(oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(password) || oConvertUtils.isEmpty(smscode)  || oConvertUtils.isEmpty(phone) ) {
            result.setMsg("重置密码失败！");
            result.setSuccess(false);
            return result;
        }

        SysUser sysUser=new SysUser();
        Object object= redisUtil.get(phone);
        if(null==object) {
        	result.setMsg("短信验证码失效！");
            result.setSuccess(false);
            return result;
        }
        if(!smscode.equals(object)) {
        	result.setMsg("短信验证码不匹配！");
            result.setSuccess(false);
            return result;
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,username).eq(SysUser::getPhone,phone));
        if (sysUser == null) {
            result.setMsg("未找到用户！");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMsg("密码重置完成！");
            return result;
        }
    }
	

	/**
	 * 根据TOKEN获取用户的部分信息（返回的数据是可供表单设计器使用的数据）
	 * 
	 * @return
	 */
	@GetMapping("/getUserSectionInfoByToken")
	public R<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
		try {
			String username = null;
			// 如果没有传递token，就从header中获取token并获取用户信息
			if (oConvertUtils.isEmpty(token)) {
				 username = JwtUtil.getUserNameByToken(request);
			} else {
				 username = JwtUtil.getUsername(token);				
			}

			log.info(" ------ 通过令牌获取部分用户信息，当前用户： " + username);

			// 根据用户名查询用户信息
			SysUser sysUser = sysUserService.getUserByName(username);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sysUserId", sysUser.getId());
			map.put("sysUserCode", sysUser.getUsername()); // 当前登录用户登录账号
			map.put("sysUserName", sysUser.getRealname()); // 当前登录用户真实名称
			map.put("sysOrgCode", sysUser.getOrgCode()); // 当前登录用户部门编号

			log.info(" ------ 通过令牌获取部分用户信息，已获取的用户信息： " + map);

			return R.ok(map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return R.error(500, "查询失败:" + e.getMessage());
		}
	}
	
	/**
	 * 【APP端接口】获取用户列表  根据用户名和真实名 模糊匹配
	 * @param keyword
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/appUserList")
	public R<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "username", required = false) String username,
			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name = "syncFlow", required = false) String syncFlow) {
		try {
			//TODO 从查询效率上将不要用mp的封装的page分页查询 建议自己写分页语句
			LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
			if(oConvertUtils.isNotEmpty(syncFlow)){
                query.eq(SysUser::getActivitiSync, "1");
            }
			query.eq(SysUser::getDelFlag,"0");
			if(oConvertUtils.isNotEmpty(username)){
			    if(username.contains(",")){
                    query.in(SysUser::getUsername,username.split(","));
                }else{
                    query.eq(SysUser::getUsername,username);
                }
            }else{
                query.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));
            }
			Page<SysUser> page = new Page<>(pageNo, pageSize);
			IPage<SysUser> res = this.sysUserService.page(page, query);
			return R.ok(res);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return R.error(500, "查询失败:" + e.getMessage());
		}
		
	}

    /**
     * 获取被逻辑删除的用户列表，无分页
     *
     * @return logicDeletedUserList
     */
    @GetMapping("/recycleBin")
    public R getRecycleBin() {
        List<SysUser> logicDeletedUserList = sysUserService.queryLogicDeleted();
        if (logicDeletedUserList.size() > 0) {
            // 批量查询用户的所属部门
            // step.1 先拿到全部的 userIds
            List<String> userIds = logicDeletedUserList.stream().map(SysUser::getId).collect(Collectors.toList());
            // step.2 通过 userIds，一次性查询用户的所属部门名字
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            logicDeletedUserList.forEach(item -> item.setOrgCode(useDepNames.get(item.getId())));
        }
        return R.ok(logicDeletedUserList);
    }

    /**
     * 还原被逻辑删除的用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
    public R putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userIds = jsonObject.getString("userIds");
        if (StringUtils.isNotBlank(userIds)) {
            SysUser updateUser = new SysUser();
            updateUser.setUpdateBy(JwtUtil.getUserNameByToken(request));
            updateUser.setUpdateTime(new Date());
            sysUserService.revertLogicDeleted(Arrays.asList(userIds.split(",")), updateUser);
        }
        return R.ok("还原成功");
    }

    /**
     * 彻底删除用户
     *
     * @param userIds 被删除的用户ID，多个id用半角逗号分割
     * @return
     */
    @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
    public R deleteRecycleBin(@RequestParam("userIds") String userIds) {
        if (StringUtils.isNotBlank(userIds)) {
            sysUserService.removeLogicDeleted(Arrays.asList(userIds.split(",")));
        }
        return R.ok("删除成功");
    }


    /**
     * 移动端修改用户信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/appEdit", method = RequestMethod.PUT)
    public R<SysUser> appEdit(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        R<SysUser> result = new R<SysUser>();
        try {
            String username = JwtUtil.getUserNameByToken(request);
            SysUser sysUser = sysUserService.getUserByName(username);
            sysBaseAPI.addLog("移动端编辑用户，id： " +jsonObject.getString("id") ,CommonConstant.LOG_TYPE_2, 2);
            if(sysUser==null) {
                result.errorMsg("未找到对应用户!");
            }else {
                String realname=jsonObject.getString("realname");
                String avatar=jsonObject.getString("avatar");
                String sex=jsonObject.getString("sex");
                String phone=jsonObject.getString("phone");
                String email=jsonObject.getString("email");
                Date birthday=jsonObject.getDate("birthday");

                sysUser.setRealname(realname);
                sysUser.setAvatar(avatar);
                sysUser.setSex(Integer.parseInt(sex));
                sysUser.setBirthday(birthday);
                sysUser.setPhone(phone);
                sysUser.setEmail(email);
                sysUser.setUpdateTime(new Date());
                sysUserService.updateById(sysUser);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.errorMsg("操作失败!");
        }
        return result;
    }
    /**
     * 根据userid获取用户信息和部门员工信息
     *
     * @return Result
     */
    @GetMapping("/queryChildrenByUsername")
    public R queryChildrenByUsername(@RequestParam("userId") String userId) {
        //获取用户信息
        Map<String,Object> map=new HashMap<String,Object>();
        SysUser sysUser = sysUserService.getById(userId);
        String username = sysUser.getUsername();
        Integer identity = sysUser.getUserIdentity();
        map.put("sysUser",sysUser);
        if(identity!=null && identity==2){
            //获取部门用户信息
            String departIds = sysUser.getDepartIds();
            if(StringUtils.isNotBlank(departIds)){
                List<String> departIdList = Arrays.asList(departIds.split(","));
                List<SysUser> childrenUser = sysUserService.queryByDepIds(departIdList,username);
                map.put("children",childrenUser);
            }
        }
        return R.ok(map);
    }
    /**
     * 移动端查询部门用户信息
     * @param departId
     * @return
     */
    @GetMapping("/appQueryByDepartId")
    public R<List<SysUser>> appQueryByDepartId(@RequestParam(name="departId", required = false) String departId) {
        R<List<SysUser>> result = new R<List<SysUser>>();
        List<String> list=new ArrayList<String> ();
        list.add(departId);
        List<SysUser> childrenUser = sysUserService.queryByDepIds(list,null);
        result.setData(childrenUser);
        return result;
    }
    /**
     * 移动端查询用户信息(通过用户名模糊查询)
     * @param keyword
     * @return
     */
    @GetMapping("/appQueryUser")
    public R<List<SysUser>> appQueryUser(@RequestParam(name = "keyword", required = false) String keyword) {
        R<List<SysUser>> result = new R<List<SysUser>>();
        LambdaQueryWrapper<SysUser> queryWrapper =new LambdaQueryWrapper<SysUser>();
        //TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.ne(SysUser::getUsername,"_reserve_user_external");
        if(StringUtils.isNotBlank(keyword)){
            queryWrapper.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));
        }
        List<SysUser> list = sysUserService.list(queryWrapper);
        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = list.stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            list.forEach(item->{
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
        result.setData(list);
        return result;
    }
}
