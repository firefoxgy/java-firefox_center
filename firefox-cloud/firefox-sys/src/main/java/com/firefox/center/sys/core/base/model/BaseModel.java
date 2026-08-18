package com.firefox.center.sys.core.base.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 树结构数据的实体类
 * <p>
 */
public class BaseModel implements Serializable{

    private static final long serialVersionUID = 1L;

    private String key;
    private String value;
    private String id;
    private String title;
    private String parentId;
    private boolean isLeaf;
    private boolean checkable=true;
    private boolean selectable=true;
    private List<BaseModel> children = new ArrayList<>();

    public BaseModel() { }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public List<BaseModel> getChildren() {
        return children;
    }

    public void setChildren(List<BaseModel> children) {
        if (children==null){
            this.isLeaf=true;
        }
        this.children = children;
    }
}
