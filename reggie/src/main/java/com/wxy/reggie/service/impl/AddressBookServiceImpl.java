package com.wxy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.reggie.entity.AddressBook;
import com.wxy.reggie.mapper.AddressBookMapper;
import com.wxy.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {


    @Autowired
    private AddressBookService addressBookService;
}
