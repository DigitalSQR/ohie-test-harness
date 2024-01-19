import error_logo from "../../../styles/images/error.png";
import "./workflow-testing.scss";
import { notification } from "antd";
export default function WorkFlowTesting(){
    const clickHandler = () => {
        notification.info({
            placement:"botton-right",
            description:"No actions yet"
        })
    }
    return (
        <div className="Workflow-testing-wrapper">
        <div className="container">
            <div className="col-12">
                <div className="d-flex justify-content-end mb-3">
                    <button className="btn btn-start"> <i className="bi bi-play-fill"></i> Start</button>
                </div>
                <div className="table-responsive mb-5">
                    <table className=" data-table">
                        <thead>
                            <tr>
                                <th>COMPONENTS</th>
                                <th>TEST CASES</th>
                                <th>RESULT</th>
                                <th>DURATION</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Client Registry - CRWF-1</td>
                                <td>Create a blank Patient resource</td>
                                <td>
                                <span id="boot-icon" className="bi bi-check-circle-fill" style={{fontSize: 2+'rem', color: 'rgb(0, 128, 55)'}}></span>
                                </td>
                                <td>22 ms</td>
                                <td className="text-end cursor-pointer"> <i className="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Add demographic information</td>
                                <td>
                                <span id="boot-icon" className="bi bi-x-circle-fill" style={{fontSize: 2+'rem', color: 'rgb(255, 0, 0)'}}></span>
                                </td>
                                <td>367 ms</td>
                                <td className="text-end cursor-pointer"> <i className="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            {/* <tr className="expand-row">
                                <td className=""></td>
                                <td colspan="4">
                                    <img src={error_logo}/>
                                </td>
                            </tr> */}
                            <tr>
                                <td></td>
                                <td>Call FHIR API to create Patient resource</td>
                                <td>
                                    <span id="boot-icon" className="bi bi-check-circle-fill" style={{fontSize: 2+'rem', color: 'rgb(0, 128, 55)'}}></span>
                                </td>
                                <td>217 ms</td>
                                <td className="text-end cursor-pointer">
                                    <i className="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Check acknowledgement message</td>
                                <td>
                                    <span id="boot-icon" className="bi bi-check-circle-fill" style={{fontSize: 2+'rem', color: 'rgb(0, 128, 55)'}}></span>
                                </td>
                                <td>564 ms</td>
                                <td className="text-end cursor-pointer">
                                    <i className="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td>Client Registry - CRWF-2</td>
                                <td>Create a blank Patient resource</td>
                                <td>
                                    <span id="boot-icon" className="bi bi-check-circle-fill" style={{fontSize: 2+'rem', color: 'rgb(0, 128, 55)'}}></span>
                                </td>
                                <td>242 ms</td>
                                <td className="text-end cursor-pointer"> <i className="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Add demographic information</td>
                                <td>
                                    <span id="boot-icon" className="bi bi-check-circle-fill" style={{fontSize: 2+'rem', color: 'rgb(0, 128, 55)'}}></span>
                                </td>
                                <td>165 ms</td>
                                <td className="text-end cursor-pointer">
                                    <i className="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Check acknowledgement message</td>
                                <td>
                                    <div className="spinner-border text-secondary" role="status">
                                    </div>
                                </td>
                                <td>165 ms</td>
                                <td className="text-end cursor-pointer">
                                    <i className="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div className="d-flex justify-content-center">
                    <button onClick={clickHandler} className="btn btn-primary btn-blue py-2 font-size-14">Generate report</button>
                </div>
            </div>
        </div>
    </div>
    )
}