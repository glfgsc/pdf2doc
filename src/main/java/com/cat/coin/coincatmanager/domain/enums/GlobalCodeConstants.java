package com.cat.coin.coincatmanager.domain.enums;

import com.cat.coin.coincatmanager.domain.pojo.Code;

/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 *
 * 一般情况下，使用 HTTP 响应状态码 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status
 * 虽然说，HTTP 响应状态码作为业务使用表达能力偏弱，但是使用在系统层面还是非常不错的
 * 比较特殊的是，因为之前一直使用 0 作为成功，就不使用 200 啦。
 *
 * @author 芋道源码
 */
public interface GlobalCodeConstants {

    Code SUCCESS = new Code(200, "success");

    // ========== 客户端错误段 ==========

    Code BAD_REQUEST = new Code(400, "请求参数不正确");
    Code UNAUTHORIZED = new Code(401, "账号未登录");
    Code ACCESS_DENIED = new Code(402,"无权限访问");
    Code FORBIDDEN = new Code(403, "没有该操作权限");
    Code NOT_FOUND = new Code(404, "请求未找到");
    Code METHOD_NOT_ALLOWED = new Code(405, "请求方法不正确");
    Code LOCKED = new Code(423, "请求失败，请稍后重试"); // 并发请求，不允许
    Code TOO_MANY_REQUESTS = new Code(429, "请求过于频繁，请稍后重试");

    // ========== 服务端错误段 ==========

    Code INTERNAL_SERVER_ERROR = new Code(500, "系统异常");
    Code NOT_IMPLEMENTED = new Code(501, "功能未实现/未开启");
    Code ERROR_CONFIGURATION = new Code(502, "错误的配置项");

    // ========== 自定义错误段 ==========
    Code REPEATED_REQUESTS = new Code(900, "重复请求，请稍后重试"); // 重复请求
    Code DEMO_DENY = new Code(901, "演示模式，禁止写操作");

    Code UNKNOWN = new Code(999, "未知错误");


    Code EMAIL_REGISTER_REPEAT = new Code(1001,"邮箱已被注册");
    Code EMAIL_ERROR_CODE = new Code(1002,"验证码错误");
    Code USER_NOT_EXIST = new Code(1003,"用户不存在");
    Code PASSWORD_ERROR = new Code(1004,"密码错误");
    Code EMAIL_NOT_EXIST = new Code(1005,"邮箱尚未注册");

}


