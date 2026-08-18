package com.firefox.center.dts.job.thread;


import com.firefox.center.common.constrains.CommonConstant;
import com.firefox.center.common.utils.AesUtil;
import com.firefox.center.dts.db.user.model.TUserApp;
import com.firefox.center.dts.db.user.service.TUserAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordThread {

	private final TUserAppService tUserAppService;

	public void execute(){
		int startIdx=0, startId=0;
		int total= tUserAppService.selectCount();
		if(total!=0){
			this.execute(startIdx, startId, total);
		}
	}

	public int execute(int startIdx, int startId, int total){
		int length=500;
		int curr=0,idx=0;
		List<TUserApp> list= tUserAppService.selectList(startId, length);
		if(list.size()!=0) {
			for (TUserApp tUserApp : list) {
				//注册手机号
				BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
				String password=encode.encode(getPhoneLastStr(tUserApp.getPhone()));

				tUserApp.setPassword(password);
				tUserApp.setPassword02(AesUtil.encode(getPhoneLastStr(tUserApp.getPhone())));
				tUserAppService.updateById(tUserApp);

				idx++;
				curr=startIdx+idx;

				System.out.println("总数："+total +"\t当前处理到第"+curr+"条");
			}
			if(curr<total){
				this.execute(curr, startId, total);
			}
		}
		return total;
	}

	protected String getPhoneLastStr(String phone){
		return phone.substring(phone.length()- CommonConstant.DEFAULT_PASSWORD_LENGTH);
	}
}