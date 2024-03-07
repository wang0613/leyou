package com.leyou.order.client;

import com.leyou.order.dto.AddressDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/4 18:02
 */
public abstract class AddressClient {

    public static final List<AddressDTO> addressList = new ArrayList<AddressDTO>() {
        {
            AddressDTO address = new AddressDTO();
            address.setId(1l);
            address.setAddress("北京路 4号");
            address.setCity("上海");
            address.setDistrict("浦东新区");
            address.setName("虎哥");
            address.setPhone("10021");
            address.setState("上海");
            address.setZipCode("21999");
            address.setIdDefault(true);

            addressList.add(address);

            AddressDTO address1 = new AddressDTO();
            address.setId(2l);
            address.setAddress("北京路 4号");
            address.setCity("北京");
            address.setDistrict("朝阳区");
            address.setName("虎哥");
            address.setPhone("10021");
            address.setState("北京");
            address.setZipCode("21999");
            address.setIdDefault(false);

            addressList.add(address1);
        }


    };

    public static AddressDTO findById(Long id) {
        for (AddressDTO addressDTO : addressList) {
            if (addressDTO.getId() == id) return addressDTO;


        }
        return null;

    }

}
