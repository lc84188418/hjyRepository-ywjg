package com.hjy.system.dao;

import com.hjy.system.entity.SysToken;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TSysTokenMapper {
    //通过tokenId获取token信息
    SysToken findByToken(@Param("accessToken") String accessToken);

    SysToken selectIpAndName(@Param("accessToken") String accessToken);
    /**
     * 通过userId查找token
     * @param fkUserId
     * @return
     */
    SysToken selectByUserId(@Param("fkUserId") String fkUserId);
    /**
     * 通过ip查找token
     * @param ip
     * @return
     */
    List<SysToken> selectByIp(@Param("ip") String ip);


    int insertToken(SysToken tokenEntity);

    int updateToken(SysToken tokenEntity);

    void deleteAll();

    void deleteToken(@Param("tokenId")String tokenId);

    String selectIpByUsername(@Param("username")String username);
    //通过tokenId获取ip
    String selectIpByTokenId(@Param("tokenId")String tokenId);

    int deleteTokenByIp(@Param("ip")String ip);

}
