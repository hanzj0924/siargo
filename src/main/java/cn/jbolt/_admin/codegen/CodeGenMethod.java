package cn.jbolt._admin.codegen;

import cn.jbolt.common.model.CodeGenModelAttr;
import cn.jbolt.core.util.JBoltArrayUtil;
import com.jfinal.kit.StrKit;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public class CodeGenMethod {
    private static final String TYPE_SINGLE_IMG = "imguploader";
    private static final String TYPE_MULTI_IMG = "imguploader_multi";
    private static final String TYPE_SINGLE_FILE = "fileuploader";
    private static final String TYPE_MULTI_FILE = "fileuploader_multi";
    private String name;
    private String value;
    private boolean isToggle;
    private boolean isUploadAction;
    private boolean hasEnableColumn;
    private boolean hasIsDeletedColumn;

    private String uploadDataName;
    private String uploadColName;
    private String uploadType;
    private List<CodeGenModelAttr> headers;
    private String toggleColumnName;//多个是逗号
    public String[] getToggleColumns(){
        if(isToggle && StrKit.notBlank(toggleColumnName)){
            return JBoltArrayUtil.from(toggleColumnName);
        }
        return null;
    }
    public CodeGenMethod(String name){
        this.name = name;
    }
    public CodeGenMethod(String name,String value){
        this.name = name;
        this.value = value;
    }
    public CodeGenMethod(String name,List<CodeGenModelAttr> headers){
        this.name = name;
        this.headers = headers;
    }
    public CodeGenMethod(String name,boolean isToggle,String toggleColumnName){
        this.name = name;
        if(!isToggle && StrKit.isBlank(toggleColumnName)){
            throw new RuntimeException("isToggle为true时 toggleColumnName必须指定");
        }
        this.isToggle = isToggle;
        this.toggleColumnName = toggleColumnName;
    }

    public CodeGenMethod(String name,boolean isToggle,String[] toggleColumnNames){
        this(name,isToggle,JBoltArrayUtil.join(toggleColumnNames,","));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isToggle() {
        return isToggle;
    }

    public void setToggle(boolean toggle) {
        isToggle = toggle;
    }

    public String getToggleColumnName() {
        return toggleColumnName;
    }

    public void setToggleColumnName(String toggleColumnName) {
        this.toggleColumnName = toggleColumnName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CodeGenModelAttr> getHeaders() {
        return headers;
    }

    public void setHeaders(List<CodeGenModelAttr> headers) {
        this.headers = headers;
    }
    public boolean getIsUploadAction() {
        return isUploadAction;
    }

    public void setIsUploadAction(boolean uploadAction) {
        isUploadAction = uploadAction;
    }

    public String getUploadColName() {
        return uploadColName;
    }

    public void setUploadColName(String uploadColName) {
        this.uploadColName = uploadColName;
    }


    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public String getUploadDataName() {
        return uploadDataName;
    }

    public void setUploadDataName(String uploadDataName) {
        this.uploadDataName = uploadDataName;
    }

    public boolean getHasEnableColumn() {
        return hasEnableColumn;
    }

    public void setHasEnableColumn(boolean hasEnableColumn) {
        this.hasEnableColumn = hasEnableColumn;
    }

    public boolean getHasIsDeletedColumn() {
        return hasIsDeletedColumn;
    }

    public void setHasIsDeletedColumn(boolean hasIsDeletedColumn) {
        this.hasIsDeletedColumn = hasIsDeletedColumn;
    }
}
