package com.liancheng.lcweb.converter;

import com.liancheng.lcweb.domain.User;
import com.liancheng.lcweb.dto.UserDTO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class User2UserDTOConverter {

    public static UserDTO convert(User user){
        UserDTO  userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        return userDTO;

    }

    public static List<UserDTO> convert(List<User> userList){

        return userList.stream().map(e -> convert(e)).collect(Collectors.toList());
    }
}
