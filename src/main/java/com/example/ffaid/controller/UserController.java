package com.example.ffaid.controller;

import com.example.ffaid.VO.Description;
import com.example.ffaid.VO.Output;
import com.example.ffaid.VO.Result;
import com.example.ffaid.VO.TelResult;
import com.example.ffaid.domain.*;
import com.example.ffaid.faceapi.FaceAdd;
import com.example.ffaid.faceapi.FaceIdentify;
import com.example.ffaid.service.Neo4jService;
import com.example.ffaid.service.UrgentTelService;
import com.example.ffaid.speechapi.AsrMain;
import com.example.ffaid.speechapi.common.DemoException;
import com.example.ffaid.util.FileUpLoadUtil;
import com.example.ffaid.util.GsonUtils;
import com.example.ffaid.util.IdUtil;
import com.example.ffaid.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;
import java.util.List;


/**
 * @author DIX
 * @date 2019/11/29 20:01
 */
@Api(value="用户controller",tags={"用户操作接口"})
@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UrgentTelService urgentTelService;

    @Autowired
    private Neo4jService neo4jService;


    /**
     * 登录
     */
    @GetMapping("/login")
    @ApiOperation(value="用户登录",tags={""},notes="用户名密码不为空")
    public User userLogin(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.userLogin(username, password);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value="获得单个用户信息",tags={""},notes="参数id在路径中带上即可")
    public User getUser(@PathVariable("id") Integer id) {
//        String auth=BaiduAuth.getAuth();
//        System.out.print(auth);
        return userService.getUser(id);
    }


    @PostMapping("")
    @ApiOperation(value="用户注册",tags={""},notes="user 为整个对象，还要传入一个图像file")
    public Object userRegister(MultipartFile file, User user) {

        System.out.println(file.isEmpty());
        String ranName=userService.handleUploadPicture(file);
        user.setPic(ranName);
        userService.userRegister(user);
        User user1 =userService.getUserByTel(user.getTel());
        FaceAdd.add(user1.getId(),ranName);
        return "success";
    }

    @PostMapping("/image")
    @ApiOperation(value="上传文件",tags={""},notes="单独的测试接口，返回文件在服务器中的路径")
    public Object upload(MultipartFile file) {

        System.out.println(file.isEmpty());
        String url=userService.handleUploadPicture(file);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/identify")
    @ApiOperation(value="用户人脸认证",tags={""},notes="如果用户识别成功，返回这个人的紧急联系人电话列表，" +
            "同时获得既往病史和其他信息供算法使用，不存在这个人返回失败")
        public Object searchFace(MultipartFile file) {

        return ResponseEntity.ok(userService.searchFace(file));
    }

    @PutMapping("/{id}")
    @ApiOperation(value="修改用户信息",tags={""},notes="")
    public Object update(@PathVariable Integer id, User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.update(user));
    }


    @ApiOperation(value="删除用户",tags={""},notes="用户名密码不为空")
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(userService.logicalDelete(id));
    }

    /**
     * 语音转文字接口
     */
    @PostMapping("/voice")
    @ApiOperation(value="语音转文字接口",tags={""},notes="")
    public Object speechIdentify(MultipartFile file) throws IOException, DemoException {
        return userService.speechIdentify(file);
    }

    /**
     * 文字获得结果接口
     */
    @GetMapping("/vWord")
    @ApiOperation(value="文字获得结果接口",tags={""},notes="")
    public Object WordIdentify(@RequestParam("word") String word) throws IOException, DemoException {
        return ResponseEntity.ok(userService.WordIdentify(word));
    }

    /**
     * 语音直接获得结果接口
     */
    @PostMapping("/voice/doc")
    @ApiOperation(value="语音直接获得结果接口",tags={""},notes="")
    public Object speechIdentifyDoc(MultipartFile file) throws IOException, DemoException {
        return ResponseEntity.ok(userService.speechIdentifyDoc(file));
    }

    @GetMapping(value = "/aidCare")
    public Object searchNode(@RequestParam("disease") String disease)throws Exception{
        return ResponseEntity.ok(neo4jService.searchNode(disease));
    }


    public static void main(String[] args) {
//        String result="呃，这个人，他口吐白沫";
//        String[] arguments = new String[] {"python","C:\\Users\\Administrator\\Desktop\\下\\OPPO\\v2\\confidenceBasedModel3.py",result};
//        String result1="";
//        try {
//
//            Process process = Runtime.getRuntime().exec(arguments);
//            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//                result1=result1+line;
//            }
//
//            in.close();
//
//            //java代码中的process.waitFor()返回值为0表示我们调用python脚本成功，
//            //返回值为1表示调用python脚本失败，这和我们通常意义上见到的0与1定义正好相反
//            int re = process.waitFor();
//            System.out.println(re);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        String result1="{\"disease\": [], \"symtoms\": \"[\"1\",\"2\"]\"}";
////        result1=result1.substring(1,result1.length()-1);
//        Description result3= GsonUtils.fromJson(result1,Description.class);
//        System.out.println(result3.getSymptoms().get(1));
//        System.out.println(result3.getDisease().get(1).getName());
//        System.out.println(result3.getDisease().get(1).getRelatedDisease());

    }
}
