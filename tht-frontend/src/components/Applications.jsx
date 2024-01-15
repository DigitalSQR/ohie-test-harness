import React from 'react'
import "./Sidebar";
import "../scss/_table.scss";
import "../scss/application-request.scss";
import sortIcon from '../styles/images/sort-icon.png'
import { useNavigate } from 'react-router-dom';
const Applications = () => {
    const navigate = useNavigate();
  return (
    <div>
      <div id="wrapper">
            <div className="col-12">
                <div className="row mb-2 justify-content-between">
                    <div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12">
                        <div className="custom-input custom-input-sm mb-3">
                            <input type="text" className="form-control" placeholder="Search"/>
                        </div>
                    </div>
                    <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
                        <div className="d-flex align-items-baseline justify-content-end">
                            <span className="pe-3 text-nowrap">Status :</span>
                            <div className="mb-3">
                                <select className="form-select custom-select custom-select-sm" aria-label="Default select example">
                                    <option selected="">Component name</option>
                                    <option value="1">One</option>
                                    <option value="2">Two</option>
                                    <option value="3">Three</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="table-responsive">
                    <table className=" data-table">
                        <thead>
                            <tr>
                                <th>APP NAME <a className="ps-1" href="#"><img src={sortIcon} alt="Sort"/></a></th>
                                <th>COMPANY NAME <a className="ps-1" href="#"><img src={sortIcon} alt="Sort"/></a></th>
                                <th>DATE OF APPLICATION</th>
                                <th>EMAIL ID <a className="ps-1" href="#"><img src={sortIcon} alt="Sort"/></a></th>
                                <th>CHOOSE ACTION</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>MedPlat</td>
                                <td>ArguSoft</td>
                                <td>12 Mar 2023</td>
                                <td>abc@efr.com</td>
                                <td><button className="btn btn-green-sm" onClick={()=>{navigate("/dashboard/choose-test")}}> <i className="bi bi-play-fill"></i> Start</button></td>
                            </tr>
                            <tr>
                                <td>Apptivate</td>
                                <td>ArguSoft</td>
                                <td>12 Mar 2023</td>
                                <td>abc@efr.com</td>
                                <td><button className="btn btn-green-sm"  onClick={()=>{navigate("/dashboard/choose-test")}}> <i className="bi bi-play-fill"></i> Start</button></td>
                            </tr>
                            <tr>
                                <td>LazyPixal</td>
                                <td>ArguSoft</td>
                                <td>12 Mar 2023</td>
                                <td>abc@efr.com</td>
                                <td><button className="btn btn-orange-sm"> <i className="bi bi-play-fill"></i> Resume</button></td>
                            </tr>
                            <tr>
                                <td>AppConnected</td>
                                <td>ArguSoft</td>
                                <td>12 Mar 2023</td>
                                <td>abc@efr.com</td>
                                <td><button className="btn btn-blue-sm" onClick={()=>{navigate("/application-report")}}> <i className="bi bi-file-text"></i> Report</button></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
  )
}

export default Applications;
