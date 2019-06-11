package com.example.myapplication.bean;

import java.util.List;

public class ShangpinBean {


    /**
     * msg : 获取信息成功
     * status : 1
     * data : [{"id":"2","name":"vivo IQOO1","picture":"/Public/Api/images/ceW4PT96QI9Mc.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"},{"id":"3","name":"vivo IQOO2","picture":"/Public/Api/images/ceW4PT96QI9Mc.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"},{"id":"4","name":"vivo IQOO","picture":"/Public/Api/images/ceW9esZnvUIRs.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"},{"id":"5","name":"vivo IQOO4","picture":"/Public/Api/images/ceW4PT96QI9Mc.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"},{"id":"6","name":"vivo IQOO5","picture":"/Public/Api/images/ceW4PT96QI9Mc.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"},{"id":"7","name":"vivo IQOO6","picture":"/Public/Api/images/ceW4PT96QI9Mc.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"},{"id":"8","name":"vivo IQOO7","picture":"/Public/Api/images/ceW4PT96QI9Mc.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"},{"id":"9","name":"vivo IQOO8","picture":"/Public/Api/images/ceW4PT96QI9Mc.jpg","price":"3299.00","old_price":"200.00","description":"4000毫安，44W快充"}]
     */

    private String msg;
    private int status;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 2
         * name : vivo IQOO1
         * picture : /Public/Api/images/ceW4PT96QI9Mc.jpg
         * price : 3299.00
         * old_price : 200.00
         * description : 4000毫安，44W快充
         */

        private String id;
        private String name;
        private String picture;
        private String price;
        private String old_price;
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getOld_price() {
            return old_price;
        }

        public void setOld_price(String old_price) {
            this.old_price = old_price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
