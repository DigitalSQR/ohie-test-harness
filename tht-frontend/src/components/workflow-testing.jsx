import error_logo from "../styles/images/error.png";
import "../scss/workflow-testing.scss";
import { notification } from "antd";
export default function WorkFlowTesting(){
    const clickHandler = () => {
        notification.info({
            placement:"botton-right",
            description:"No actions yet"
        })
    }
    return (
        <div class="Workflow-testing-wrapper">
        <div class="container">
            <div class="col-12">
                <div class="d-flex justify-content-end mb-3">
                    <button class="btn btn-start"> <i class="bi bi-play-fill"></i> Start</button>
                </div>
                <div class="table-responsive mb-5">
                    <table class=" data-table">
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
                                    <span class="result-pass">Pass</span>
                                </td>
                                <td>22 ms</td>
                                <td class="text-end cursor-pointer"> <i class="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Add demographic information</td>
                                <td>
                                    <span class="result-fail">Fail</span>
                                </td>
                                <td>367 ms</td>
                                <td class="text-end cursor-pointer"> <i class="bi bi-chevron-down"></i>
                                </td>
                            </tr>
                            <tr class="expand-row">
                                <td class=""></td>
                                <td colspan="4">
                                    <img src={error_logo}/>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Call FHIR API to create Patient resource</td>
                                <td>
                                    <span class="result-pass">Pass</span>
                                </td>
                                <td>217 ms</td>
                                <td class="text-end cursor-pointer">
                                    <i class="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Check acknowledgement message</td>
                                <td>
                                    <span class="result-pass">Pass</span>
                                </td>
                                <td>564 ms</td>
                                <td class="text-end cursor-pointer">
                                    <i class="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td>Client Registry - CRWF-2</td>
                                <td>Create a blank Patient resource</td>
                                <td>
                                    <span class="result-pass">Pass</span>
                                </td>
                                <td>242 ms</td>
                                <td class="text-end cursor-pointer"> <i class="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Add demographic information</td>
                                <td>
                                    <span class="result-pass">Pass</span>
                                </td>
                                <td>165 ms</td>
                                <td class="text-end cursor-pointer">
                                    <i class="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>Check acknowledgement message</td>
                                <td>
                                    <span class="running">Running</span>
                                </td>
                                <td>165 ms</td>
                                <td class="text-end cursor-pointer">
                                    <i class="bi bi-chevron-right"></i>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-center">
                    <button onClick={clickHandler} class="btn btn-primary btn-blue py-2 font-size-14">Generate report</button>
                </div>
            </div>
        </div>
    </div>
    )
}