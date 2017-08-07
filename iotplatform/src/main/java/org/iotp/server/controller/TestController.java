package org.iotp.server.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.iotp.server.IoTPServerApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/noauth/test")
@Slf4j
public class TestController extends BaseController {

  @RequestMapping(value = "/now", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, String> now() {
    Map<String, String> jo = new HashMap<>();
    jo.put("now", Calendar.getInstance().getTime().toString());
    log.info("get......");
    return jo;
  }

  @RequestMapping(value = "/baidu.js", method = RequestMethod.GET)
  @ResponseBody
  public String baidujs() {
    String readFileToString = "";
    try {
      // System.out.println(ThingsboardServerApplication.class.getResourceAsStream("../../../baidu_map.js"));
      readFileToString = IOUtils.toString(IoTPServerApplication.class.getResourceAsStream("../../../baidu_map.js"),
          "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return readFileToString;
  }

  @RequestMapping(value = "/post", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> post() {
    Map<String, String> source = new HashMap<>();
    source.put("now", Calendar.getInstance().getTime().toString());
    log.info("post......");
    return source;
  }

  @RequestMapping(value = "/put", method = RequestMethod.PUT)
  @ResponseBody
  public Map<String, String> put() {
    Map<String, String> source = new HashMap<>();
    source.put("now", Calendar.getInstance().getTime().toString());
    log.info("put......");
    return source;
  }

  @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
  @ResponseBody
  public Map<String, String> delete(@RequestBody Map<String, String> source) {
    source.put("now", Calendar.getInstance().getTime().toString());
    log.info("delete......");
    return source;
  }

  @RequestMapping(value = "/head", method = RequestMethod.HEAD)
  @ResponseBody
  public Map<String, String> head() {
    Map<String, String> source = new HashMap<>();
    source.put("now", Calendar.getInstance().getTime().toString());
    log.info("head......");
    return source;
  }

  @RequestMapping(value = "/patch", method = RequestMethod.PATCH)
  @ResponseBody
  public Map<String, String> patch(@RequestBody Map<String, String> source) {
    source.put("now", Calendar.getInstance().getTime().toString());
    log.info("patch......");
    return source;
  }
}
