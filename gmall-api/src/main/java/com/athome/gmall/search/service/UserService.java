package com.athome.gmall.search.service;

import com.athome.gmall.bean.UmsMember;
import com.athome.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
