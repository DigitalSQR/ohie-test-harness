import React from "react";
import Select from "react-select";

/**
 * CustomSelect Component:
 * This component provides a customizable Select component using react-select.
 * It allows handling change and blur events and supports multi-select functionality.
 *
 * Props:
 * - className: Additional CSS class names for styling the Select component.
 * - placeholder: Placeholder text for the Select component.
 * - field: Field object from Formik containing the field's name, value, onChange, onBlur, etc.
 * - form: Formik form object containing form-wide metadata and functions.
 * - options: Array of options to populate the Select component.
 * - isMulti: Boolean flag indicating whether the Select component supports multi-select (default: false).
 */

const CustomSelect = ({
  className,
  placeholder,
  field,
  form,
  options,
  isMulti = false
}) => {
  // Function to handle the change event of the Select component
  const onChange = (option) => {
     // Update the form field value based on whether it's multi-select or not
    form.setFieldValue(
      field.name,
      isMulti
        ? option.map((item) => item.value)
        : option.value
    );
  };

   // Function to handle the blur event of the Select component
  const onBlur = (option) => {
    // Set the field as touched when it loses focus
    form.setFieldTouched(
      field.name,
      true
    );
  };

    // Function to get the initial value of the Select component
  const getValue = () => {
    // If options and field value exist, return selected options; otherwise, return empty array or string
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
