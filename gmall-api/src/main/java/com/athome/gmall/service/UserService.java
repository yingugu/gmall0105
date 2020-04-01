package com.athome.gmall.service;

import com.athome.gmall.bean.UmsMember;
import com.athome.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);

    void addOauthUser(UmsMember umsMember);
}
