package com.hjy.system.entity;

import lombok.Data;

/**
 * @author liuchun
 * @createDate 2020/11/19 14:09
 * @Classname ReUserPerms
 * @Description TODO
 */
@Data
public class ReUserPerms {
    private String pk_userPerms_id;
    private String fk_user_id;
    private String fk_perms_id;
}
