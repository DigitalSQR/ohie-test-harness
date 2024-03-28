import React from 'react'
import {
    RiseOutlined,
  } from "@ant-design/icons";
const Statistics = (props) => {
  return (
    <div className="container">
    <div className="d-flex justify-content-between align-items-center p-3 border rounded card flex-row">
      <div className='text-start'>
        <p className="mb-2 ">{props.parameter}</p>
        <p className="m-0 fs-3 fw-bold ">{props.value}</p>
      </div>
      <RiseOutlined style={{ fontSize: "40px" }} />
    </div>
  </div>
  )
}

export default Statistics
// style={{ maxWidth: "300px" }}