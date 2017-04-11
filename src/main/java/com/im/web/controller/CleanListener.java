package com.im.web.controller;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.im.web.service.TempIndexService;

public class CleanListener implements ServletContextListener{
	private Timer timer;
	private WebApplicationContext wac = null;
	private String realPath = null;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("=== 程序关闭 ===");
		if(timer != null){
			timer.cancel();
		}
	}
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("=== 清理程序 启动 ===");
		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
		tempIndexCommit();
		realPath = sce.getServletContext().getRealPath("");
		timer = new Timer();
		timer.scheduleAtFixedRate(new IndexCommit(), 50000, 300000);
	}
	/**
	 * 程序启动，把tempIndex里的数据拿出来添加索引
	 */
	public void tempIndexCommit(){
		System.out.println("=== 清理tempIndex ===");
		TempIndexService indexService = (TempIndexService)wac.getBean("tempIndexService");
		indexService.updateSetIndex();
	}
	/**
	 * 提交索引到硬盘类
	 *
	 */
	public class IndexCommit extends TimerTask{
		private TempIndexService indexService;
		@Override
		public void run() {
			indexService = (TempIndexService)wac.getBean("tempIndexService");
			indexService.commitIndex();
		}
		
	}
}
