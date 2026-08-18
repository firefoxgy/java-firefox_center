package com.firefox.center.dts.job.thread;


import com.firefox.center.common.Record;
import com.firefox.center.common.constants.Consts;
import com.firefox.center.common.constrains.CommonConstant;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.utils.AesUtil;
import com.firefox.center.common.utils.DateUtil;
import com.firefox.center.common.utils.IdGen;
import com.firefox.center.credit.feign.CreditFeignService;
import com.firefox.center.credit.feign.pojo.FeignTCredit;
import com.firefox.center.credit.feign.pojo.FeignTUserInfo;
import com.firefox.center.dts.db.ucenter.common.model.*;
import com.firefox.center.dts.db.ucenter.dg181.service.*;
import com.firefox.center.dts.db.user.model.*;
import com.firefox.center.dts.db.user.service.*;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Dg181Thread {

	private static String appId="kpc3nsde893fkssvjt9k";
	private static int tenantId=10011;

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

	private final TCreditService tCreditService;
	private final TCreditLogService tCreditLogService;

	private final CreditFeignService creditFeignService;

	public void execute(){
		int startIdx=0, startId=0;
		TUserDtsLog tUserDtsLog=tUserDtsLogService.selectRecord(appId, tenantId);
		if(tUserDtsLog!=null){
			startIdx=tUserDtsLog.getCurr();
			startId=tUserDtsLog.getUid();
		}
		List<TUsership> thirdShipList=tUsershipService.selectList();
		List<TUsership> otherThirdShipList=tUsershipService.selectOtherList();
		List<TUsership> shipList = Lists.newArrayList();
		shipList.addAll(thirdShipList);
		for(TUsership tUsership:otherThirdShipList){
			shipList.add(
					TUsership.builder()
					.uid(tUsership.getOtheruid())
					.otheruid(tUsership.getUid())
					.build()

			);
		}
		int total= ucMembersService.selectCount();
		if(total!=0){
			this.execute(startIdx, startId, total, shipList);
		}
		deleteThirdShip(appId, tenantId);

		saveThirdShip(thirdShipList);
		saveOtherThirdShip(otherThirdShipList);
	}

	public int execute(int startIdx, int startId, int total, List<TUsership> shipList){
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

					//计算用户积分
					saveCredit(member.getUid(), 0);

					//保存排行用户信息
					FeignTUserInfo feignTUserInfo = FeignTUserInfo.builder()
							.appId(appId)
							.tenantId(tenantId)
							.uid(member.getUid().longValue())
							.type("app")
							.nickname(member.getNickname())
							.headerImg(member.getHeadpic())
							.build();
					if (StrKit.notBlank(member.getUsername())) {
						feignTUserInfo.setUsername(member.getUsername());
					} else {
						feignTUserInfo.setUsername(getHidePhone(member.getMobile()));
					}
					if (StrKit.notNull(member.getSex())) {
						feignTUserInfo.setGender(member.getSex());
					}
					saveUserInfo(feignTUserInfo);

				} else if (!"reg".equals(member.getSource())) {
					TUserThird tUserThird = null;
					FeignTUserInfo feignTUserInfo = null;
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

							//排行用户信息
							feignTUserInfo = FeignTUserInfo.builder()
									.appId(appId)
									.tenantId(tenantId)
									.uid(tUserApple.getUid().longValue())
									.type("third")
									.build();
							if (StrKit.notBlank(tUserApple.getHeadpic())) {
								feignTUserInfo.setHeaderImg(tUserApple.getHeadpic());
							}
							if (StrKit.notBlank(tUserApple.getNickname2())) {
								feignTUserInfo.setNickname(tUserApple.getNickname2());
							} else {
								feignTUserInfo.setNickname(tUserApple.getNickname());
							}
							if (StrKit.notNull(tUserApple.getSex2())) {
								feignTUserInfo.setGender(tUserApple.getSex2());
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

							//排行用户信息
							feignTUserInfo = FeignTUserInfo.builder()
									.appId(appId)
									.tenantId(tenantId)
									.uid(tUserMp.getUid().longValue())
									.type("third")
									.build();
							if (StrKit.notBlank(tUserMp.getHeadpic())) {
								feignTUserInfo.setHeaderImg(tUserMp.getHeadpic());
							} else if (StrKit.notBlank(tUserMp.getHeadimgurl())) {
								feignTUserInfo.setHeaderImg(tUserMp.getHeadimgurl());
							}
							if (StrKit.notBlank(tUserMp.getNickname2())) {
								feignTUserInfo.setNickname(tUserMp.getNickname2());
							} else {
								feignTUserInfo.setNickname(tUserMp.getNickname());
							}
							if (StrKit.notNull(tUserMp.getSex2())) {
								feignTUserInfo.setGender(tUserMp.getSex2());
							} else if (StrKit.notNull(tUserMp.getSex())) {
								feignTUserInfo.setGender(tUserMp.getSex());
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

							//排行用户信息
							feignTUserInfo = FeignTUserInfo.builder()
									.appId(appId)
									.tenantId(tenantId)
									.uid(tUserQq.getUid().longValue())
									.type("third")
									.build();
							if (StrKit.notBlank(tUserQq.getHeadpic())) {
								feignTUserInfo.setHeaderImg(tUserQq.getHeadpic());
							} else if (StrKit.notBlank(tUserQq.getFigureurl())) {
								feignTUserInfo.setHeaderImg(tUserQq.getFigureurl());
							}
							if (StrKit.notBlank(tUserQq.getNickname2())) {
								feignTUserInfo.setNickname(tUserQq.getNickname2());
							} else {
								feignTUserInfo.setNickname(tUserQq.getNickname());
							}
							if (StrKit.notNull(tUserQq.getSex2())) {
								feignTUserInfo.setGender(tUserQq.getSex2());
							} else if (StrKit.notNull(tUserQq.getGender())) {
								feignTUserInfo.setGender(tUserQq.getGender());
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

							//排行用户信息
							feignTUserInfo = FeignTUserInfo.builder()
									.appId(appId)
									.tenantId(tenantId)
									.uid(tUserWeibo.getUid().longValue())
									.type("third")
									.build();
							if (StrKit.notBlank(tUserWeibo.getHeadpic())) {
								feignTUserInfo.setHeaderImg(tUserWeibo.getHeadpic());
							} else if (StrKit.notBlank(tUserWeibo.getHeadimage())) {
								feignTUserInfo.setHeaderImg(tUserWeibo.getHeadimage());
							}
							if (StrKit.notBlank(tUserWeibo.getNickname2())) {
								feignTUserInfo.setNickname(tUserWeibo.getNickname2());
							} else {
								feignTUserInfo.setNickname(tUserWeibo.getNickname());
							}
							if (StrKit.notNull(tUserWeibo.getSex2())) {
								feignTUserInfo.setGender(tUserWeibo.getSex2());
							} else if (StrKit.notNull(tUserWeibo.getGender())) {
								feignTUserInfo.setGender(tUserWeibo.getGender());
							}
						}
					}
					if (tUserThird != null) {
						tUserThirdService.save(tUserThird);
						int uid=0;
						for(TUsership tUsership:shipList){
							if(tUsership.getOtheruid()==tUserThird.getSid().intValue()){
								uid=tUsership.getUid();
								break;
							}
						}
						//计算用户积分
						saveCredit(uid, tUserThird.getSid().intValue());
						//保存排行用户信息
						saveUserInfo(feignTUserInfo);
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
				this.execute(curr, startId, total, shipList);
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

	protected void saveCredit(Integer uid, Integer sid){
		Date date = new Date();
		long dayStartTime=DateUtil.parseTime(DateUtil.getDateYMD(date)+" 00:00:00");
		long dayEndTime=DateUtil.parseTime(DateUtil.format(DateUtil.getDate(date, 1), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

		long weekStartTime=DateUtil.parseTime(DateUtil.format(DateUtil.getWeekStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
		long weekEndTime=DateUtil.parseTime(DateUtil.format(DateUtil.getWeekEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

		long monthStartTime=DateUtil.parseTime(DateUtil.format(DateUtil.getMonthStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
		long monthEndTime=DateUtil.parseTime(DateUtil.format(DateUtil.getMonthEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

		long seasonStartTime=DateUtil.parseTime(DateUtil.format(DateUtil.getSeasonStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
		long seasonEndTime=DateUtil.parseTime(DateUtil.format(DateUtil.getSeasonEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

		long yearStartTime=DateUtil.parseTime(DateUtil.format(DateUtil.getYearStart(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");
		long yearEndTime=DateUtil.parseTime(DateUtil.format(DateUtil.getYearEnd(), DateUtil.DATE_FORMAT_YMD)+" 00:00:00");

		int creditDay=0, creditWeek=0, creditMonth=0, creditSeason=0, creditYear=0;
		TCreditLog tCreditDay=tCreditLogService.selectCredit(uid, dayStartTime, dayEndTime);
		if(tCreditDay!=null && tCreditDay.getSnum()!=null && tCreditDay.getSnum()!=0){
			creditDay=tCreditDay.getSnum();
		}

		TCreditLog tCreditWeek=tCreditLogService.selectCredit(uid, weekStartTime, weekEndTime);
		if(tCreditWeek!=null && tCreditWeek.getSnum()!=null && tCreditWeek.getSnum()!=0){
			creditWeek=tCreditWeek.getSnum();
		}

		TCreditLog tCreditMonth=tCreditLogService.selectCredit(uid, monthStartTime, monthEndTime);
		if(tCreditMonth!=null && tCreditMonth.getSnum()!=null && tCreditMonth.getSnum()!=0){
			creditMonth=tCreditMonth.getSnum();
		}

		TCreditLog tCreditSeason=tCreditLogService.selectCredit(uid, seasonStartTime, seasonEndTime);
		if(tCreditSeason!=null && tCreditSeason.getSnum()!=null && tCreditSeason.getSnum()!=0){
			creditSeason=tCreditSeason.getSnum();
		}

		TCreditLog tCreditYear=tCreditLogService.selectCredit(uid, yearStartTime, yearEndTime);
		if(tCreditYear!=null && tCreditYear.getSnum()!=null && tCreditYear.getSnum()!=0){
			creditYear=tCreditYear.getSnum();
		}
		FeignTCredit feignTCredit = FeignTCredit.builder()
				.appId(appId)
				.tenantId(tenantId)
				.uid(uid.longValue())
				.sid(sid.longValue())
				.creditDay(creditDay)
				.creditWeek(creditWeek)
				.creditMonth(creditMonth)
				.creditSeason(creditSeason)
				.creditYear(creditYear)
				.createTime(date)
				.updateTime(date)
				.build();
		creditFeignService.saveCredit(feignTCredit);
	}

	protected void saveUserInfo(FeignTUserInfo feignTUserInfo){
		creditFeignService.saveUserInfo(feignTUserInfo);
	}

	protected String getHidePhone(String phone){
		return phone.substring(0,3)+"****"+phone.substring(phone.length()- 4);
	}


	protected String getPhoneLastStr(String phone){
		return phone.substring(phone.length()- CommonConstant.DEFAULT_PASSWORD_LENGTH);
	}

}