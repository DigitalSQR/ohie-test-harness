import React from "react";
import Select from "react-select";

const CustomSelect = ({
  className,
  placeholder,
  field,
  form,
  options,
  isMulti = false
}) => {
  const onChange = (option) => {
    form.setFieldValue(
      field.name,
      isMulti
        ? option.map((item) => item.value)
        : option.value
    );
  };

  const onBlur = (option) => {
    form.setFieldTouched(
      field.name,
      true
    );
  };

  const getValue = () => {
    if (options && field.value) {
      return isMulti
        ? options.filter((option) => field.value.indexOf(option.value) >= 0)
        : options.find((option) => option.value === field.value);
    } else {
      return isMulti ? [] : "";
    }
  };
  

  return (
    <Select
      className={className}
      name={field.name}
      value={getValue()}
      onChange={onChange}
      onBlur={onBlur}
      placeholder={placeholder}
      options={options}
      isMulti={isMulti}
    />
  );
};

export default CustomSelect;
