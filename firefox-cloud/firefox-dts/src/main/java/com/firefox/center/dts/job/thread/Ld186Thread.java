package com.firefox.center.dts.job.thread;


import com.firefox.center.common.Record;
import com.firefox.center.common.constants.Consts;
import com.firefox.center.common.constrains.CommonConstant;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.utils.AesUtil;
import com.firefox.center.common.utils.IdGen;
import com.firefox.center.dts.db.ucenter.common.model.*;
import com.firefox.center.dts.db.ucenter.ld186.service.*;
import com.firefox.center.dts.db.user.model.*;
import com.firefox.center.dts.db.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Ld186Thread {

	private static String appId="kpc3nsde893fkssvjt9k";
	private static int tenantId=10014;

	private final TUserAppService tUserAppService;
	private final UcMembersService ucMembersService;
	private final TUserAppleService tUserAppleService;
	private final TUserMpService tUserMpService;
	private final TUserQqService tUserQqService;
	private final TUserWeiboService tUserWeiboService;
	private final TUsershipService tUsershipService;
	private final TUserThirdService tUserThirdService;
	private final TUserOneService tUserOneService;
	private final TUserAppThirdService tUserAppThirdService;
	private final TUserDtsLogService tUserDtsLogService;

	public void execute(){
		int startIdx=0, startId=0;
		TUserDtsLog tUserDtsLog=tUserDtsLogService.selectRecord(appId, tenantId);
		if(tUserDtsLog!=null){
			startIdx=tUserDtsLog.getCurr();
			startId=tUserDtsLog.getUid();
		}
		int total= ucMembersService.selectCount();
		if(total!=0){
			this.execute(startIdx, startId, total);
		}
		deleteThirdShip(appId, tenantId);
		saveThirdShip(tUsershipService.selectList());
		saveOtherThirdShip(tUsershipService.selectOtherList());
	}

	public int execute(int startIdx, int startId, int total){
		int length=500;
		int curr=0,idx=0;
		List<UcMembers> list= ucMembersService.selectList(startId, length);
		if(list.size()!=0) {
			for (UcMembers member : list) {
				//手机号不为空的设置为主帐号
				if (StrKit.notBlank(member.getMobile())) {
					TUserOne tUserOne=tUserOneService.selectRecord(member.getMobile());
					String openId="";
					Long openIntId=0L;
					if(tUserOne==null){
						Record record= IdGen.getMd5Id();
						openId=record.getStr("md5");
						openIntId=record.getLong("id");
						tUserOne=TUserOne.builder()
								.id(IdGen.getId())
								.openId(openId)
								.openIntId(openIntId)
								.phone(member.getMobile())
								.build();
						tUserOneService.save(tUserOne);
					}else{
						openId=tUserOne.getOpenId();
						openIntId=tUserOne.getOpenIntId();
					}

					TUserApp tUserApp = TUserApp.builder()
							.id(IdGen.getId())
							.openIntId(openIntId)
							.openId(openId)
							.uid(member.getUid().longValue())
							.phone(member.getMobile())
							.nickname(member.getNickname())
							.headerImg(member.getHeadpic())
							.appId(appId)
							.tenantId(tenantId)
							.source(0)
							.build();
					if (StrKit.notBlank(member.getUsername())) {
						tUserApp.setUsername(member.getUsername());
					} else {
						tUserApp.setUsername(member.getMobile());
					}
					if(StrKit.notBlank(member.getPassword())){
						tUserApp.setPassword(member.getPassword());
						tUserApp.setPwdEncrypt(2);
					}else{
						BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
						String password=encode.encode(getPhoneLastStr(member.getMobile()));
						tUserApp.setPassword(password);
						tUserApp.setPassword02(AesUtil.encode(getPhoneLastStr(member.getMobile())));
						tUserApp.setSalt(member.getSalt());
						tUserApp.setPwdEncrypt(1);
					}

					if (StrKit.notBlank(member.getEmail())) {
						tUserApp.setEmail(member.getEmail());
					}
					if (StrKit.notNull(member.getSex())) {
						tUserApp.setGender(member.getSex());
					}
					tUserAppService.save(tUserApp);
				} else if (!"reg".equals(member.getSource())) {
					TUserThird tUserThird = null;
					if ("apple".equals(member.getSource())) {
						TUserApple tUserApple = tUserAppleService.selectRecord(member.getUid());
						if (tUserApple != null) {
							tUserThird = TUserThird.builder()
									.id(IdGen.getId())
									.sid(tUserApple.getUid().longValue())
									.loginType(Consts.grantType.APPLEID)
									.externalappid(tUserApple.getExternalappid())
									.thirdid(tUserApple.getIdentifier())
									.appId(appId)
									.tenantId(tenantId)
									.build();
							if (StrKit.notBlank(tUserApple.getHeadpic())) {
								tUserThird.setFigureurl(tUserApple.getHeadpic());
							}
							if (StrKit.notBlank(tUserApple.getNickname2())) {
								tUserThird.setNickname(tUserApple.getNickname2());
							} else {
								tUserThird.setNickname(tUserApple.getNickname());
							}
							if (StrKit.notNull(tUserApple.getSex2())) {
								tUserThird.setGender(tUserApple.getSex2());
							}
						}
					} else if ("mp".equals(member.getSource())) {
						TUserMp tUserMp = tUserMpService.selectRecord(member.getUid());
						if (tUserMp != null) {
							tUserThird = TUserThird.builder()
									.id(IdGen.getId())
									.sid(tUserMp.getUid().longValue())
									.loginType(Consts.grantType.WEIXIN)
									.externalappid(tUserMp.getExternalappid())
									.thirdid(tUserMp.getOpenid())
									.thirdUnionId(tUserMp.getUnionid())
									.appId(appId)
									.tenantId(tenantId)
									.build();
							if (StrKit.notBlank(tUserMp.getHeadpic())) {
								tUserThird.setFigureurl(tUserMp.getHeadpic());
							} else if (StrKit.notBlank(tUserMp.getHeadimgurl())) {
								tUserThird.setFigureurl(tUserMp.getHeadimgurl());
							}
							if (StrKit.notBlank(tUserMp.getNickname2())) {
								tUserThird.setNickname(tUserMp.getNickname2());
							} else {
								tUserThird.setNickname(tUserMp.getNickname());
							}
							if (StrKit.notNull(tUserMp.getSex2())) {
								tUserThird.setGender(tUserMp.getSex2());
							} else if (StrKit.notNull(tUserMp.getSex())) {
								tUserThird.setGender(tUserMp.getSex());
							}
							if (StrKit.notBlank(tUserMp.getCountry())) {
								tUserThird.setCountry(tUserMp.getCountry());
							}
							if (StrKit.notBlank(tUserMp.getProvince())) {
								tUserThird.setProvince(tUserMp.getProvince());
							}
							if (StrKit.notBlank(tUserMp.getCity())) {
								tUserThird.setCity(tUserMp.getCity());
							}
						}
					} else if ("qq".equals(member.getSource())) {
						TUserQq tUserQq = tUserQqService.selectRecord(member.getUid());
						if (tUserQq != null) {
							tUserThird = TUserThird.builder()
									.id(IdGen.getId())
									.sid(tUserQq.getUid().longValue())
									.loginType(Consts.grantType.QQ)
									.externalappid(tUserQq.getExternalappid())
									.thirdid(tUserQq.getOpenid())
									.appId(appId)
									.tenantId(tenantId)
									.build();
							if (StrKit.notBlank(tUserQq.getHeadpic())) {
								tUserThird.setFigureurl(tUserQq.getHeadpic());
							} else if (StrKit.notBlank(tUserQq.getFigureurl())) {
								tUserThird.setFigureurl(tUserQq.getFigureurl());
							}
							if (StrKit.notBlank(tUserQq.getNickname2())) {
								tUserThird.setNickname(tUserQq.getNickname2());
							} else {
								tUserThird.setNickname(tUserQq.getNickname());
							}
							if (StrKit.notNull(tUserQq.getSex2())) {
								tUserThird.setGender(tUserQq.getSex2());
							} else if (StrKit.notNull(tUserQq.getGender())) {
								tUserThird.setGender(tUserQq.getGender());
							}
							if (StrKit.notBlank(tUserQq.getCountry())) {
								tUserThird.setCountry(tUserQq.getCountry());
							}
							if (StrKit.notBlank(tUserQq.getProvince())) {
								tUserThird.setProvince(tUserQq.getProvince());
							}
							if (StrKit.notBlank(tUserQq.getCity())) {
								tUserThird.setCity(tUserQq.getCity());
							}
						}
					} else if ("weibo".equals(member.getSource())) {
						TUserWeibo tUserWeibo = tUserWeiboService.selectRecord(member.getUid());
						if (tUserWeibo != null) {
							tUserThird = TUserThird.builder()
									.id(IdGen.getId())
									.sid(tUserWeibo.getUid().longValue())
									.loginType(Consts.grantType.WEIBO)
									.externalappid(tUserWeibo.getExternalappid())
									.thirdid(tUserWeibo.getWid())
									.appId(appId)
									.tenantId(tenantId)
									.build();
							if (StrKit.notBlank(tUserWeibo.getHeadpic())) {
								tUserThird.setFigureurl(tUserWeibo.getHeadpic());
							} else if (StrKit.notBlank(tUserWeibo.getHeadimage())) {
								tUserThird.setFigureurl(tUserWeibo.getHeadimage());
							}
							if (StrKit.notBlank(tUserWeibo.getNickname2())) {
								tUserThird.setNickname(tUserWeibo.getNickname2());
							} else {
								tUserThird.setNickname(tUserWeibo.getNickname());
							}
							if (StrKit.notNull(tUserWeibo.getSex2())) {
								tUserThird.setGender(tUserWeibo.getSex2());
							} else if (StrKit.notNull(tUserWeibo.getGender())) {
								tUserThird.setGender(tUserWeibo.getGender());
							}
							if (StrKit.notBlank(tUserWeibo.getLocation())) {
								tUserThird.setCountry(tUserWeibo.getLocation());
							}
							if (StrKit.notBlank(tUserWeibo.getProvince())) {
								tUserThird.setProvince(tUserWeibo.getProvince());
							}
							if (StrKit.notBlank(tUserWeibo.getCity())) {
								tUserThird.setCity(tUserWeibo.getCity());
							}
						}
					}
					if (tUserThird != null) {
						tUserThirdService.save(tUserThird);
					}
				}
				if(member.getUid()>startId){
					startId=member.getUid();
				}

				idx++;
				curr=startIdx+idx;
				TUserDtsLog tUserDtsLog=tUserDtsLogService.selectRecord(appId, tenantId);
				if(tUserDtsLog==null){
					tUserDtsLog = TUserDtsLog.builder()
							.appId(appId)
							.tenantId(tenantId)
							.total(total)
							.curr(curr)
							.uid(startId)
							.startTime(new Date())
							.endTime(new Date())
							.build();
					tUserDtsLogService.save(tUserDtsLog);
				}else{
					tUserDtsLog.setTotal(total);
					tUserDtsLog.setCurr(curr);
					tUserDtsLog.setUid(startId);
					tUserDtsLog.setEndTime(new Date());
					tUserDtsLogService.updateById(tUserDtsLog);
				}
			}
			if(curr<total){
				this.execute(curr, startId, total);
			}
		}
		return total;
	}

	protected void deleteThirdShip(String appId, int tenantId){
		tUserAppThirdService.deleteRecord(appId, tenantId);
	}

	protected void saveThirdShip(List<TUsership> list){
		if(list.size()!=0){
			for(TUsership tUsership:list){
				TUserAppThird tUserAppThird=TUserAppThird.builder()
						.id(IdGen.getId())
						.uid(tUsership.getUid().longValue())
						.sid(tUsership.getOtheruid().longValue())
						.appId(appId)
						.tenantId(tenantId)
						.build();
				tUserAppThirdService.save(tUserAppThird);
			}
		}
	}

	protected void saveOtherThirdShip(List<TUsership> list){
		if(list.size()!=0){
			for(TUsership tUsership:list){
				TUserAppThird tUserAppThird=TUserAppThird.builder()
						.id(IdGen.getId())
						.uid(tUsership.getOtheruid().longValue())
						.sid(tUsership.getUid().longValue())
						.appId(appId)
						.tenantId(tenantId)
						.build();
				tUserAppThirdService.save(tUserAppThird);
			}
		}
	}

	protected String getPhoneLastStr(String phone){
		return phone.substring(phone.length()- CommonConstant.DEFAULT_PASSWORD_LENGTH);
	}
}