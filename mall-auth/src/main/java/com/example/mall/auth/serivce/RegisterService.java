package com.example.mall.auth.serivce;

import com.example.mall.common.model.vo.RegisterVo;
import com.example.mall.common.model.result.Result;

public interface RegisterService {
    Result sendCheckCode(String mailToAddress);

    boolean checkCode(String mail, String checkCode);

    Result register(RegisterVo registerVo);

    void removeCheckCode(String mail);
}
