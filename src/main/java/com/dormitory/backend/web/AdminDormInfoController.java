package com.dormitory.backend.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.dormitory.backend.config.Code;
import com.dormitory.backend.config.MyException;
import com.dormitory.backend.pojo.*;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@RestController
public class AdminDormInfoController {
    //尚未完成用户权限控制以方便调试
    @Autowired
    private DormitoryService dormitoryService;
    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping(value = "api/admin/addDormitory")
    @ResponseBody
    public Dormitory addDormitory(@RequestBody Dormitory dormitory){
        if(dormitory==null||dormitory.getGender().getGender()==null||dormitory.getDegree().getDegree()==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        dormitory.setGender(dormitory.getGender());
        dormitory.setDegree(dormitory.getDegree());
        return dormitoryService.addDormitory(dormitory);
    }
    @CrossOrigin
    @PostMapping(value = "api/admin/modifyDormitory")
    @ResponseBody
    public Dormitory modifyDormitory(@RequestBody Dormitory dormitory){
        if(dormitory==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        Dormitory dor_DB = dormitoryService.findById(dormitory.getId());
        if(dor_DB==null){
            throw new MyException(Code.DORMITORY_NOT_EXIST);
        }
        if (dormitory.getGender()!=null && !Objects.equals(dormitory.getGender(), dor_DB.getGender()))
            dor_DB.setGender(dormitory.getGender());
        if (dormitory.getDegree()!=null && !Objects.equals(dormitory.getDegree(), dor_DB.getDegree()))
            dor_DB.setDegree(dormitory.getDegree());
        if (dormitory.getFloor()!=0 && dormitory.getFloor()!=dor_DB.getFloor())
            dor_DB.setFloor(dormitory.getFloor());
        if (dormitory.getBuildingName()!=null && !Objects.equals(dormitory.getBuildingName(), dor_DB.getBuildingName()))
            dor_DB.setBuildingName(dormitory.getBuildingName());
        if (dormitory.getHouseNum()!=null && !Objects.equals(dormitory.getHouseNum(), dor_DB.getHouseNum()))
            dor_DB.setHouseNum(dormitory.getHouseNum());
        if  (dormitory.getLocation()!=null && !Objects.equals(dormitory.getLocation(), dor_DB.getLocation()))
            dor_DB.setLocation(dormitory.getLocation());
        if (dormitory.getBed()!=0 && dormitory.getBed()!=dor_DB.getBed())
            dor_DB.setBed(dormitory.getBed());
        return dormitoryService.addDormitory(dormitory);
    }

    @CrossOrigin
    @PostMapping(value = "api/admin/removeDormitory")
    @ResponseBody
    public Dormitory removeDormitory(@RequestBody Dormitory dormitory){
        if(dormitory==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        if(dormitoryService.checkRoomExisted(dormitory)==null){
            throw new MyException(Code.DORMITORY_NOT_EXIST);
        }
        dormitoryService.removeDormitory(dormitory);
        return dormitory;
    }


    public void exportSelectionInformation(){

    }

    /*导出周计划*/
//    https://huaweicloud.csdn.net/63876ecfdacf622b8df8c069.html
    @CrossOrigin
    @PostMapping(path = "api/admin/selectionInfo/export")
    @ResponseBody
    public void exportSelectionInformationExcel(@RequestParam(required = false) String location,
                                                     @RequestParam(required = false) String buildingName,
                                                     @RequestParam(required = false) String floor,
                                                     @RequestParam(required = false) String houseNum,
                                                     @RequestParam(required = false) String fileName,
                                                     HttpServletResponse response) throws Exception{
        if(fileName==null){
            fileName = "Dormitory_Selection_Information";
        }
        location = location==null?"":location;
        buildingName = buildingName==null?"":buildingName;
        floor = floor==null?"":floor;
        houseNum = houseNum==null?"":houseNum;

        //文件名含中文需要转码
        String fileNameFull =
                URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8);
        //将需要导出的数据从数据库中查出
        List<SelectionInfoExcelData> list = dormitoryService.getSelectionInfo(location, buildingName, floor, houseNum);

        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置头居中
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //内容策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置 水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle); //由于自定义了合并策略，所以此处默认合并策略并未使用
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileNameFull);
        //将OutputStream对象附着到EasyExcel的ExcelWriter实例
        EasyExcel.write(response.getOutputStream(), SelectionInfoExcelData.class) //（输出流， 文件头）
                .excelType(ExcelTypeEnum.XLSX)
                .autoCloseStream(false)
                .sheet("Dormitory Selection Information") //第一个sheet的名
                .doWrite(list); //写入数据
    }

    @CrossOrigin
    @PostMapping(value = "api/admin/setSelectionTime")
    @ResponseBody
    public String setSelectionTime(@RequestBody SelectionTimeConfig config){
        SelectionTimeConfig config_ = buildConfig(config);
        SelectionTimeConfig configInDB = dormitoryService.getSelectionTime(config_.getGender(),config_.getDegree());
        if (configInDB!=null){
            configInDB.setStartTime(config.getStartTime());
            configInDB.setEndTime(config.getEndTime());
            dormitoryService.setSelectionTime(configInDB);
//            注意这不是异常。
            return "modify an existed time slot";
        } else {
            dormitoryService.setSelectionTime(config_);
            return "create a new time slot";
        }
    }

    @CrossOrigin
    @PostMapping(value = "api/admin/deleteSelectionTime")
    @ResponseBody
    public void deleteSelectionTime(@RequestBody SelectionTimeConfig config){
        SelectionTimeConfig config_ = buildConfig(config);
        SelectionTimeConfig configInDB = dormitoryService.getSelectionTime(config_.getGender(),config_.getDegree());
        if (configInDB==null){
            throw new MyException(Code.GENERAL_NOT_EXIST);
        }
        dormitoryService.deleteSelectionTime(configInDB);
    }

//    模板方法
    public SelectionTimeConfig buildConfig(SelectionTimeConfig config){
        if(config.getGender()==null||config.getDegree()==null){
            throw new MyException(Code.MISSING_FIELD);
        }
        Gender gender = config.getGender();
        Degree degree = config.getDegree();
        if(gender==null||degree==null){
            throw new MyException(Code.GENERAL_NOT_EXIST);
        }
        config.setDegree(degree);
        config.setGender(gender);
        return config;
    }

}
