import React, { useEffect, useState } from "react";
import { Table, Alert, List, Collapse, Empty} from "antd";
import { useDispatch } from "react-redux";
import { set_header } from "../../../../reducers/homeReducer";
import "../ValidateConfigFacts/ValidateConfigFacts.scss";
import { ComponentAPI } from "../../../../api/ComponentAPI";
import { useLoader } from "../../../loader/LoaderContext";
import { RefObjUriConstants } from "../../../../constants/refObjUri_constants";
const { Panel } = Collapse;

const ValidateConfigFacts = ({
    refObjUri,
    refId
}) => {

    //states
    const [validationResults, setValidationResults] = useState([]);
    const [validationResultTreeData, setValidationResultTreeData] = useState([]);
    const { showLoader, hideLoader } = useLoader();

    const dispatch = useDispatch();

    const fetchValidationData = () => {
        showLoader();
        ComponentAPI.validateConfiguration()
        .then((response) => { 
            setValidationResults(response.data);
        })
        .catch((error) => {
            
        })
        .finally(() => {
            hideLoader();
        });
    };

    useEffect(() => {
        dispatch(set_header("Configuration Validation Result"));
        fetchValidationData();
    },[]);

    const columns = [
        {
          title: 'Element',
          dataIndex: 'element',
          key: 'element'
        },
        {
            title: 'Entity',
            dataIndex: 'refObjUri',
            key: 'refObjUri',
            width: '10%',
            render: (text, record) =>{
                switch(text){
                    case RefObjUriConstants.COMPONENT_REFOBJURI:
                        return"Component";
                    case RefObjUriConstants.SPECIFICATION_REFOBJURI:
                        return "Specification";
                    case RefObjUriConstants.TESTCASE_REFOBJURI:
                        return "Test Case";
                    default:
                        return "";
                }
            }
        },
        {
          title: 'Message',
          dataIndex: 'title',
          width: '50%',
          key: 'title',
          render : (text, record) =>{
            if (text && record.errorLevel === 'OK') {
                return (
                  <Alert message={text} type="success" />
                );
              } else if (record.errorLevel === 'ERROR') {
                return (
                  <Alert message={`[Issue] ${text}`} type="error" />
                );
              } else if (record.errorLevel === 'WARN') {
                return (
                  <Alert message={`[Warning] ${text}`} type="warning" />
                );
              }
              // Add more conditions as needed
              return null;
          }
        },
      ];

      useEffect(() => {
        // Function to recursively filter array elements
        const filterArray = (arr) => {
            return arr.filter(item => {
                if (item.errorLevel === "ERROR" || item.errorLevel === "WARN") {
                    return true;
                } else if (item.children && item.children.length > 0) {
                    item.children = filterArray(item.children);
                    return item.children.length > 0;
                }
                return false;
            }).map(item => {
                if (item.children && item.children.length === 0) {
                    delete item.children;
                }
                return item;
            });
        }
    
        let index = 0;
        let treeData = [];
    
        while (RefObjUriConstants.COMPONENT_REFOBJURI === validationResults[index]?.refObjUri) {
            const componentNode = createNode(validationResults[index],index);
            index++;
    
            while (RefObjUriConstants.SPECIFICATION_REFOBJURI === validationResults[index]?.refObjUri) {
                const specNode = createNode(validationResults[index],index);
                componentNode.children.push(specNode);
                index++;
    
                while (RefObjUriConstants.TESTCASE_REFOBJURI === validationResults[index]?.refObjUri) {
                    const testCaseNode = createNode(validationResults[index],index);
                    specNode.children.push(testCaseNode);
                    index++;
                }
            }
    
            treeData.push(componentNode);
        }
    
        const filteredData = filterArray(treeData);
        setValidationResultTreeData(filteredData);
    }, [validationResults]);
    
    // Function to create a node object
    const createNode = (data,index) => ({
        key: index,
        title: data.message,
        element: data.element,
        errorLevel: data.level,
        refObjUri: data.refObjUri,
        children: []
    });

    const data = [
        '1. When a component or specification is inactive, it will be marked as a warning.',
        '2. If an active component lacks active specifications, it will be marked as an issue.',
        '3. If an active specification lacks active testcases, it will be marked as an issue.',
        '4. If an active manual testcase lacks active options, it will be flagged as an issue.',
        '5. If an active manual testcase is missing at least one active "true" option, it will also be flagged as an issue.'
      ];

    const tableDescription = () => {
        return (
            <Collapse>
                <Panel header="What Does This Data Represent?" key="1">
                    <List
                    bordered
                    header={<div style={{ fontWeight: "500" }}>This table highlights warnings and issues identified during the validation process, indicating whether the configuration accurately reflects the data or not.</div>}
                    dataSource={data}
                    renderItem={(item) => (
                        <List.Item>
                        {item}
                        </List.Item>
                    )}
                    />
                </Panel>
            </Collapse>
        );
    }

    return (
        <>
        <div id="componentList">    
        <div id="wrapper">
            {(validationResultTreeData  && validationResultTreeData.length > 0) ?
                <Table columns={columns}
                    title={tableDescription}
                    bordered 
                    className="data-table"
                    dataSource={validationResultTreeData} 
                    pagination={false}
                    expandable={{defaultExpandAllRows:true, indentSize: 24}}>
                </Table> : <Empty description="There are no validation results found, and all configurations are accurate." />
            }
            </div>
        </div>
        </>
    );
};

export default ValidateConfigFacts;