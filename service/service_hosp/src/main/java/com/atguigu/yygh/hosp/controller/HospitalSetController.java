package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    private HospitalSetService hospitalSetService;


    // 1、查询医院设置表所有信息
    // 返回jackson
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    // 2、逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospitalSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 3、条件查询带分页 需要采用post提交才能得到RequestBody的值
    @PostMapping("findPageHosp/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  // 通过Json格式传数据，可以为空
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        // 创建page对象，传递当前页，每一页的记录数
        Page<HospitalSet> page = new Page<>(current,limit);
        // 构造条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hoscode = hospitalSetQueryVo.getHoscode(); // 医院编号
        String hosname = hospitalSetQueryVo.getHosname(); // 医院名称
        if(!StringUtils.isEmpty(hosname)) {
            // 模糊查询(第二个参数通过VO对象传过来)
            wrapper.like("hosname",hosname);
        }
        if(!StringUtils.isEmpty(hoscode)){
            // 根据编号查询
            wrapper.eq("hoscode",hoscode);
        }
        // 调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);
        return Result.ok(pageHospitalSet);
    }

    // 4、添加医院设置
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        // 设置状态 1可以使用 0不能使用
        hospitalSet.setStatus(1);
        // 签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }



    // 5、根据id获取医院设置
    @GetMapping("getHospSet/{id}")
    public Result getHospSetById(@PathVariable long id){
        HospitalSet hospitalSetById = hospitalSetService.getById(id);
        return Result.ok(hospitalSetById);
    }

    // 6、修改医院设置
    @PutMapping("updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        if(b) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    // 7、批量删除医院设置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospSet(@RequestBody List<Long> idList){
        boolean b = hospitalSetService.removeByIds(idList);
        if(b) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    // 8、医院设置锁定和解锁
    @PutMapping("lockHospSet/{id}/{status}")
    public Result lockHospSetById(@PathVariable long id,
                                  @PathVariable int status){
        // 根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 设置状态
        hospitalSet.setStatus(status);
        // 调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    // 9、发送签名秘钥
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }


    // 注入service
    @Autowired
    public void setHospitalSetService(HospitalSetService hospitalSetService) {
        this.hospitalSetService = hospitalSetService;
    }


}
