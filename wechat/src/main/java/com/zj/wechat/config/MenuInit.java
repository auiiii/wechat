/*
package com.zj.wechat.config;

import com.zj.wechat.service.WeChatService;
import com.zj.wechat.util.JsonFileUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

*/
/**
 * 初始化菜单
 *//*

@Configuration
public class MenuInit implements CommandLineRunner {

    @Resource
    private WeChatService service;

    @Override
    public void run(String... args) throws Exception {
        //菜单已经打开, 就不重新注册了
        if(service.isOpenMenu())
        {
            return;
        }
        String menu = JsonFileUtil.getJsonResource("menu").toString();
        if(!"{}".equals(menu) && null != menu)
        {
            // 个人订阅号无法实现
            //service.createMenu(menu);
        }
    }
}
*/
