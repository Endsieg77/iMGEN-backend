package com.example.server.imgen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.imgen.pojo.User;
import com.example.server.imgen.pojo.imgenSession;
import com.example.server.imgen.service.IUserService;
import com.example.server.imgen.service.MailService;
import com.example.server.imgen.service.IPasswordService;
import com.example.server.imgen.service.IRSADecoderService;
import com.example.server.imgen.service.ISessionService;
import com.example.server.imgen.vo.NuserRaw;
import com.example.server.imgen.vo.NuserVo;
import com.example.server.imgen.vo.ResetPwd;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import java.sql.Timestamp;

@RestController
@RequestMapping("/user")
@Slf4j
public class userController {
    @Autowired
    private IUserService userService;

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private IPasswordService password;

    @Autowired
    private IRSADecoderService rsaDecoder;

    @Autowired
    private MailService mailService;

    static final String client_url = "http://localhost:3000";
    static final String user_url = client_url + "/user";

    @GetMapping("/login")
    public ResponseEntity login(@RequestParam String email, @RequestParam String pwd) throws Exception {
        var in = getClass().getResourceAsStream("/keypair/private.key");
        byte[] privateKeyBytes = in.readAllBytes();
        var privateKeyString = new String(privateKeyBytes);
        var pwdDecoded = rsaDecoder.decode(privateKeyString, pwd);
        QueryWrapper<User> query = new QueryWrapper<>();
        var emailDecoded = Base64.decodeBase64(email);

        query.eq("email", emailDecoded);

        var user = userService.getOne(query);
        var hash = user.getHash();
        var hashIn = password.generateHash(pwdDecoded, user.getSalt());

        if (hash.equals(hashIn)) {
            var userVo = onLogin(emailDecoded, user);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userVo);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Wrong Email Address or Password!");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody NuserRaw user) throws Exception {
        var pwd = user.getPwd();
        var salt = password.getSalt32();
        // var nuser = new User();
        var session = new imgenSession();

        var in = getClass().getResourceAsStream("/keypair/private.key");
        byte[] privateKeyBytes = in.readAllBytes();
        var privateKeyString = new String(privateKeyBytes);
        var nameDecoded = Base64.decodeBase64(user.getName());
        var emailDecoded = Base64.decodeBase64(user.getEmail());
        var pwdDecoded = rsaDecoder.decode(privateKeyString, pwd);
        var hashValue = password.generateHash(pwdDecoded, salt);

        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", emailDecoded);

        // email already used by someone
        if (userService.exists(query)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Email Already Used!");
        }

        // nuser.setName(new String(nameDecoded));
        // nuser.setEmail(new String(emailDecoded));
        // nuser.setSalt(salt);
        // nuser.setHash(hashValue);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        var sessionStr = Base64.encodeBase64String((emailDecoded +
                now.toString() +
                password.getSalt(8))
                .getBytes(Charsets.UTF_8));

        session.setName(new String(nameDecoded));
        session.setSession(sessionStr);
        session.setCreateTime(now);
        session.setEmail(new String(emailDecoded));
        session.setSalt(salt);
        session.setHash(hashValue);
        session.setTypeof("signup");

        // var nuserVo = new NuserVo();

        // BeanUtils.copyProperties(nuser, nuserVo);
        // BeanUtils.copyProperties(nuser, session);

        var verifyEmailUrl = user_url + "/verifyEmail/" + sessionStr;
        var subject = "Last step: verify your E-Mail for iMGEN!";
        var content = ("Dear %s,\n" +
                "We send you this Mail for identity authentication. \n" +
                "If you have just signed up for iMGEN premium user, " +
                "you need one last step before finally becoming a premium user of iMGEN! \n" +
                "If not, ignore this. \n" +
                "Click the link below to complete your sign-up: \n" +
                "%s\n" +
                "iMGEN Developer Team")
                .formatted(new String(nameDecoded), verifyEmailUrl);

        sessionService.save(session);
        mailService.sendSimpleMail(new String(emailDecoded), subject, content);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(verifyEmailUrl);
        // try {
        // // userService.save(nuser);

        // } catch (Exception e) {
        // return ResponseEntity
        // .status(HttpStatus.FORBIDDEN)
        // .body("Email Already Used!");
        // }
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity verifyEmail(@RequestParam String session) throws Exception {
        try {
            log.info(session);
            QueryWrapper<imgenSession> query = new QueryWrapper<>();
            query.eq("session", session).eq("typeof", "signup");
            var sessionInfo = sessionService.getOne(query);
            log.info(sessionInfo.getSession());
            var user = new User();
            BeanUtils.copyProperties(sessionInfo, user);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp then = sessionInfo.getCreateTime();
            long hours_gone = (now.getTime() - then.getTime()) / (1000 * 60 * 60);

            if (hours_gone < 1) {
                userService.save(user);
                NuserVo userVo = new NuserVo();
                BeanUtils.copyProperties(user, userVo);
                sessionService.remove(query);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(userVo);
            } else {
                sessionService.remove(query);

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("session Expired!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("session Not Found!");
        }
    }

    @PostMapping("/forgot")
    public ResponseEntity forgot(@RequestParam String email) throws Exception {
        QueryWrapper<User> query = new QueryWrapper<>();
        var emailDecoded = Base64.decodeBase64(email);
        query.eq("email", emailDecoded);

        if (userService.exists(query)) {
            var session = new imgenSession();
            session.setTypeof("forgot");
            session.setEmail(new String(emailDecoded));
            Timestamp now = new Timestamp(System.currentTimeMillis());
            session.setCreateTime(now);
            var sessionStr = Base64.encodeBase64String((emailDecoded +
                    now.toString() +
                    password.getSalt(8))
                    .getBytes(Charsets.UTF_8));
            session.setSession(sessionStr);

            sessionService.save(session);

            var resetPwdUrl = user_url + "/resetPwd/" + sessionStr;
            var subject = "Reset your password for iMGEN!";
            var content = ("Dear iMGEN user,\n" +
                    "We send you this Mail for your resetting password. \n" +
                    "If you have just required to reset password, " +
                    "reset your password in the following link: \n" +
                    "%s\n" +
                    "If not, ignore this. \n" +
                    "iMGEN Developer Team")
                    .formatted(resetPwdUrl);

            mailService.sendSimpleMail(new String(emailDecoded), subject, content);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("sent an email");
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("no previous records");
        }
    }

    @PostMapping("/resetPwd")
    public ResponseEntity resetPwd(@RequestBody ResetPwd reset) throws Exception {
        var session = reset.getSession();
        var pwd     = reset.getPwd();
        
        QueryWrapper<imgenSession> querySession = new QueryWrapper<>();
        querySession.eq("session", session).eq("typeof", "forgot");

        if (sessionService.exists(querySession)) {
            var sessionInfo = sessionService.getOne(querySession);
            var then = sessionInfo.getCreateTime();

            Timestamp now = new Timestamp(System.currentTimeMillis());
            long hours_gone = (now.getTime() - then.getTime()) / (1000 * 60 * 60);

            if (hours_gone < 1) {
                UpdateWrapper<User> updateUser = new UpdateWrapper<>();
                var email = sessionInfo.getEmail();
                updateUser.eq("email", email);

                var in = getClass().getResourceAsStream("/keypair/private.key");
                byte[] privateKeyBytes = in.readAllBytes();
                var privateKeyString = new String(privateKeyBytes);
                var salt = password.getSalt32();
                var pwdDecoded = rsaDecoder.decode(privateKeyString, pwd);
                var hashValue = password.generateHash(pwdDecoded, salt);

                updateUser.set("salt", salt).set("hash", hashValue);
                userService.update(updateUser);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("reset successful");
            } else {
                sessionService.remove(querySession);

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("session Expired!");
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("session inexists!");
        }
    }

    @GetMapping("/autologin")
    public ResponseEntity autoLogin(@RequestParam String token) throws Exception {
        QueryWrapper<User> query = new QueryWrapper<>();

        query.eq("token", token);

        var user = userService.getOne(query);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp then = user.getLastLogin();
        long days_gone = (now.getTime() - then.getTime()) / (1000 * 60 * 60 * 24);

        log.info("auto");

        if (days_gone < 14) {
            String email = user.getEmail();
            NuserVo userVo = onLogin(email.getBytes(), user);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userVo);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("must login again manually :-(");
        }

    }

    private NuserVo onLogin(byte[] email, User user) {
        var userVo = new NuserVo();
        UpdateWrapper<User> update = new UpdateWrapper<>();

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String token = Base64.encodeBase64String((email + now.toString()).getBytes());

        update.eq("email", email);
        user.setLastLogin(now);
        user.setToken(token);
        userService.update(user, update);
        BeanUtils.copyProperties(user, userVo);

        return userVo;
    }
}
