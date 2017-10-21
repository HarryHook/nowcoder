package com.nowcoder.model;


import org.springframework.stereotype.Component;

/*
每次访问，这个用户是谁
 */
@Component
public class HostHolder {
    private  static ThreadLocal<User>   users = new  ThreadLocal<User>();
    public  User getUser() {
        return users.get();
    }

    public void setUser(User user) {
       users.set(user);
    }
    public void clear() {
        users.remove();
    }
}
