package com.firefox.center.sys.core.oss.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.R;
import com.firefox.center.sys.common.system.query.QueryGenerator;
import com.firefox.center.sys.core.oss.entity.OSSFile;
import com.firefox.center.sys.core.oss.service.IOSSFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/sys/oss/file")
@RequiredArgsConstructor
public class OSSFileController {

	private final IOSSFileService ossFileService;

	@ResponseBody
	@GetMapping("/list")
	public R<IPage<OSSFile>> queryPageList(OSSFile file,
										   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
										   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
										   HttpServletRequest req) {
		R<IPage<OSSFile>> result = new R<>();
		QueryWrapper<OSSFile> queryWrapper = QueryGenerator.initQueryWrapper(file, req.getParameterMap());
		Page<OSSFile> page = new Page<>(pageNo, pageSize);
		IPage<OSSFile> pageList = ossFileService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setData(pageList);
		return result;
	}

	@ResponseBody
	@PostMapping("/upload")
	//@RequiresRoles("super")
	public R upload(@RequestParam("file") MultipartFile multipartFile) {
		R result = new R();
		try {
			ossFileService.upload(multipartFile);
			result.okMsg("上传成功！");
		}
		catch (Exception ex) {
			log.info(ex.getMessage(), ex);
			result.errorMsg("上传失败");
		}
		return result;
	}

	@ResponseBody
	@DeleteMapping("/delete")
	public R delete(@RequestParam(name = "id") String id) {
		R result = new R();
		OSSFile file = ossFileService.getById(id);
		if (file == null) {
			result.errorMsg("未找到对应实体");
		}
		else {
			boolean ok = ossFileService.delete(file);
			if (ok) {
				result.okMsg("删除成功!");
			}
		}
		return result;
	}

	/**
	 * 通过id查询.
	 */
	@ResponseBody
	@GetMapping("/queryById")
	public R<OSSFile> queryById(@RequestParam(name = "id") String id) {
		R<OSSFile> result = new R<>();
		OSSFile file = ossFileService.getById(id);
		if (file == null) {
			result.errorMsg("未找到对应实体");
		}
		else {
			result.setData(file);
			result.setSuccess(true);
		}
		return result;
	}

}
