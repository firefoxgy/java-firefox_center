package com.firefox.center.sys.core.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * <p>
 * 树结构数据的实体类
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TreeModel implements Serializable{

    private static final long serialVersionUID = 1L;

    private String key;
    private String value;
    private String id;
    private String title;
    private String parentId;
    private boolean isLeaf;
    private boolean checkable=true;
    private boolean selectable=true;
    private List<TreeModel> children = new ArrayList<>();

    public void setChildren(List<TreeModel> children) {
        if (children==null){
            this.isLeaf=true;
        }
        this.children = children;
    }

}
