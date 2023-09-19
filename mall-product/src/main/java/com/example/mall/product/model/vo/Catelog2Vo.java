package com.example.mall.product.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Catelog2Vo {
    private String catalog1Id;
    private List<Catelog3Vo> catalog3List;
    private String id;
    private String name;

    public Catelog2Vo() {
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Catelog3Vo {
        private String catalog2Id;
        private String id;
        private String name;

        public Catelog3Vo() {
        }
    }

}
