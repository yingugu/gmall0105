package com.athome.gmall.user.impl;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.athome.gmall.bean.UmsMember;
import com.athome.gmall.bean.UmsMemberReceiveAddress;
import com.athome.gmall.service.UserService;
import com.athome.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.athome.gmall.user.mapper.UserMapper;
import com.athome.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {
       List<UmsMember> umsMemberList = userMapper.selectAll();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

        Example e = new Example(UmsMemberReceiveAddress.class);
        e.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(e);

        //另一种写法,这种是查询时常用的写法，上面的selectbyexample是用来更新的，经常会跟在update后面
//        封装的参数对象
//        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
//        umsMemberReceiveAddress.setMemberId(memberId);
//         List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            //缓存结构：user:username:info 或 user:username:password 或 user:password:info
            //另一种结构： key:username+md5(password) value:用户信息
            if (jedis!=null){
                String umsMemberStr = jedis.get("user:"+umsMember.getPassword()+":info");
                if (StringUtils.isNotEmpty(umsMemberStr)){
                    UmsMember umsMemberFromCatch = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberFromCatch;

                }
                //                else{
//                    //密码错误
//                    //缓存中没有，开启数据库查询
//                    //因为开启数据库查询的方法要调用两次，所以直接提出来写
//                    UmsMember umsMemberFromDb =  loginFromDb(umsMember);
//                    if (umsMemberFromDb!=null){
//                        jedis.setex("user:" +
//                                umsMember.getPassword() + ":info",60*60*24,JSON.toJSONString(umsMemberFromDb));
//                    }
//                    //因为如果查询不到的话，返回的也是空值，所以这里直接返回查询结果就行
//                    return umsMemberFromDb;
//
//                }

            }
            //            else{
//                //开启数据库
//                //redis失效的情况下，要配置分布式锁,开启数据库
//
//
//            }
            UmsMember umsMemberFromDb =  loginFromDb(umsMember);
            if (umsMemberFromDb!=null){
                jedis.setex("user:" +
                        umsMember.getPassword() + ":info",60*60*24,JSON.toJSONString(umsMemberFromDb));
            }
            //因为如果查询不到的话，返回的也是空值，所以这里直接返回查询结果就行
            return umsMemberFromDb;


        } finally {

        }
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = null;
        try {
           jedis = redisUtil.getJedis();
           jedis.setex("user:"+memberId+":token",2*60*60,token);
        } finally {

            jedis.close();
        }


    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        //有可能出现两个完全相同的用户名密码，但是不用管，因为这是注册功能的bug，这里可以不做考虑，但是为了保证功能无问题
        List<UmsMember> umsMembers = userMapper.select(umsMember);
        if (umsMember!=null){
            return umsMembers.get(0);
        }

        return null;
    }
    // public UserMapper
}
