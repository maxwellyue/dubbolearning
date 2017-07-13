package com.maxwell.dubbolearning.web;

import com.maxwell.dubbolearning.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/************************************************************************************
 * 功能描述：
 * 创建人：岳增存  yuezc@seentao.com
 * 创建时间： 2017年07月12日 --  下午7:52 
 * 其他说明：
 * 修改时间：
 * 修改人：
 *************************************************************************************/
@RestController
public class TestController {

    @Autowired
    private TestService testService;


    @RequestMapping("/test/str")
    public String testString(String str){
        testService.test();
        String s = testService.testString(str);
        return s;
    }
}
