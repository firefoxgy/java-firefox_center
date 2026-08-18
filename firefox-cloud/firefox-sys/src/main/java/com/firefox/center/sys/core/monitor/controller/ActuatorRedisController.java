package com.firefox.center.sys.core.monitor.controller;

import com.firefox.center.common.R;
import com.firefox.center.sys.core.monitor.domain.RedisInfo;
import com.firefox.center.sys.core.monitor.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sys/actuator/redis")
@RequiredArgsConstructor
public class ActuatorRedisController {

    private final RedisService redisService;

    /**
     * Redis详细信息
     * @return
     * @throws Exception
     */
    @GetMapping("/info")
    public R<?> getRedisInfo() throws Exception {
        List<RedisInfo> infoList = this.redisService.getRedisInfo();
        log.info(infoList.toString());
        return R.ok(infoList);
    }

    @GetMapping("/keysSize")
    public Map<String, Object> getKeysSize() throws Exception {
        return redisService.getKeysSize();
    }

    @GetMapping("/memoryInfo")
    public Map<String, Object> getMemoryInfo() throws Exception {
        return redisService.getMemoryInfo();
    }
    
  //update-begin--Author:zhangweijian  Date:20190425 for：获取磁盘信息
  	/**
  	 * @功能：获取磁盘信息
  	 * @param request
  	 * @param response
  	 * @return
  	 */
  	@GetMapping("/queryDiskInfo")
  	public R<List<Map<String,Object>>> queryDiskInfo(HttpServletRequest request, HttpServletResponse response){
  		R<List<Map<String,Object>>> res = new R<>();
  		try {
  			// 当前文件系统类
  	        FileSystemView fsv = FileSystemView.getFileSystemView();
  	        // 列出所有windows 磁盘
  	        File[] fs = File.listRoots();
  	        log.info("查询磁盘信息:"+fs.length+"个");
  	        List<Map<String,Object>> list = new ArrayList<>();
  	        
  	        for (int i = 0; i < fs.length; i++) {
  	        	if(fs[i].getTotalSpace()==0) {
  	        		continue;
  	        	}
  	        	Map<String,Object> map = new HashMap<>();
  	        	map.put("name", fsv.getSystemDisplayName(fs[i]));
  	        	map.put("max", fs[i].getTotalSpace());
  	        	map.put("rest", fs[i].getFreeSpace());
  	        	map.put("restPPT", (fs[i].getTotalSpace()-fs[i].getFreeSpace())*100/fs[i].getTotalSpace());
  	        	list.add(map);
  	        	log.info(map.toString());
  	        }
  	        res.setData(list);
  	        res.setMsg("查询成功");
  		} catch (Exception e) {
  			res.error("查询失败"+e.getMessage());
  		}
  		return res;
  	}
  	//update-end--Author:zhangweijian  Date:20190425 for：获取磁盘信息
}
