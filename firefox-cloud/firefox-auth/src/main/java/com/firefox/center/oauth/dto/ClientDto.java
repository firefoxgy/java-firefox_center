package com.firefox.center.oauth.dto;

import com.firefox.center.oauth.db.model.OauthClientDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class ClientDto extends OauthClientDetails {
    private static final long serialVersionUID = 1475637288060027265L;

    private List<Long> permissionIds;

    private Set<Long> serviceIds;
}
