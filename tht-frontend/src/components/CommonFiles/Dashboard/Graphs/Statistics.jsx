import React from 'react'
import {
    RiseOutlined,
  } from "@ant-design/icons";
const Statistics = (props) => {
  return (
    <div className="container" style={{ maxWidth: "300px" }}>
    <div className="d-flex justify-content-between align-items-center p-3 border rounded">
      <div>
        <p className="m-0 fw-bold">{props.parameter}</p>
        <p className="m-0 fs-5">{props.value}</p>
      </div>
      <RiseOutlined style={{ fontSize: "40px" }} />
    </div>
  </div>
  )
}

export default Statistics
