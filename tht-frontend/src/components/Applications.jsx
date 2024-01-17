import React, { useEffect, useState } from 'react'
import "./Sidebar";
import "../scss/_table.scss";
import "../scss/application-request.scss";
import sortIcon from '../styles/images/sort-icon.png'
import { useNavigate } from 'react-router-dom';
import { StateClasses, TestRequestActionStateLabels, TestRequestStateConstants } from '../constants/test_requests_constants.js';
import { useLoader } from './loader/LoaderContext';
import { USER_ROLES } from '../constants/role_constants';
import { TestRequestAPI } from '../api/TestRequestAPI';
import { notification } from "antd";
import { formatDate } from '../utils/utils';
import UserIdEmailConnector from "./connectors/UserIdEmailConnector/UserIdEmailConnector";

const Applications = () => {
    const testRequestStates = [...TestRequestActionStateLabels, { label: "All", value: '' }];
    const [filterState, setFilterState] = useState(TestRequestStateConstants.TEST_REQUEST_STATUS_ACCEPTED);
    const [testRequests, setTestRequests] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        var state = filterState;
        if (!state || state == '') {
            state = TestRequestActionStateLabels.map(state => state.value);
        }
        if (state) {
            TestRequestAPI.getTestRequestsByState(state)
                .then((res) => {
                    setTestRequests(res.content);
                }).catch((err) => {
                    notification.error({
                        placement: "bottomRight",
                        message: 'Oops! Error fetching test requests!'
                    });
                    console.log(err);
                });
        }
    }, [filterState])

    return (
        <div>
            <div id="wrapper">
                <div className="col-12">
                    <div className="row mb-2 justify-content-between">
                        <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
                            <div className="d-flex align-items-baseline">
                                <span className="pe-3 text-nowrap">Status :</span>
                                <div className="mb-3">
                                    <select
                                        onChange={(e) => { setFilterState(e.target.value) }}
                                        value={filterState}
                                        class="form-select custom-select custom-select-sm"
                                        aria-label="Default select example"
                                    >
                                        {
                                            testRequestStates.map(testRequestState => (
                                                <option value={testRequestState.value}>{testRequestState.label}</option>
                                            ))
                                        }
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="table-responsive">
                        <table className=" data-table">
                            <thead>
                                <tr>
                                    <th>APP NAME <a className="ps-1" href="#"><img src={sortIcon} alt="Sort" /></a></th>
                                    <th>COMPANY NAME <a className="ps-1" href="#"><img src={sortIcon} alt="Sort" /></a></th>
                                    <th>DATE OF APPLICATION</th>
                                    <th>EMAIL ID <a className="ps-1" href="#"><img src={sortIcon} alt="Sort" /></a></th>
                                    <th>CHOOSE ACTION</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    testRequests.length === 0 ?
                                        <>
                                            <tr>
                                                <td className="text-center" colSpan={5}>No test requests found. Register one or wait for test request to be approved</td>
                                            </tr>
                                        </>
                                        : null
                                }
                                {
                                    testRequests?.map((testRequest) => (
                                        <tr>
                                            <td>{testRequest.name}</td>
                                            <td>{testRequest.productName}</td>
                                            <td>{formatDate(testRequest.meta.updatedAt)}</td>
                                            <td><UserIdEmailConnector userId={testRequest.assesseeId}></UserIdEmailConnector></td>
                                            <td><button className={StateClasses[testRequest.state]?.btnClass} onClick={() => { navigate("/dashboard/choose-test") }}> <i className={StateClasses[testRequest.state]?.iconClass}></i> {StateClasses[testRequest.state]?.btnText}</button></td>
                                        </tr>
                                    ))
                                }
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Applications;
