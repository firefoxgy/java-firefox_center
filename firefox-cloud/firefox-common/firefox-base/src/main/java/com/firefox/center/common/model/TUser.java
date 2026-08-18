package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 用户实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TUser implements UserDetails {
    private static final long serialVersionUID = -5886012896705137070L;

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private Long sid;
    private Long uid;
    private String openId;
    private String username;
    private String password;

    @Override
    public String getUsername() {
        Long a=getId();
        return "app_"+getId();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Sets.newHashSet();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}