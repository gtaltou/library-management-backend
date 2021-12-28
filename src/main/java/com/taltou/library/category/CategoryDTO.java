package com.taltou.library.category;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Category Model")
public class CategoryDTO implements Comparable<CategoryDTO> {

    public CategoryDTO() {
    }

    public CategoryDTO(String code, String label) {
        super();
        this.code = code;
        this.label = label;
    }

    @ApiModelProperty(value = "Category code")
    private String code;

    @ApiModelProperty(value = "Category label")
    private String label;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int compareTo(CategoryDTO o) {
        return label.compareToIgnoreCase(o.label);
    }

}

