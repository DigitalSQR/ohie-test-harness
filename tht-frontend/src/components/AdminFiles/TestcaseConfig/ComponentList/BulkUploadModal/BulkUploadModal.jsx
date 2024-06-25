import React, { useState } from "react";
import { Modal, Button, notification } from "antd";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import { DeleteOutlined } from "@ant-design/icons";
import Tooltip from "react-bootstrap/Tooltip";
import "./BulkUploadModal.scss";
import { TestCaseAPI } from "../../../../../api/TestCaseAPI";
const BulkUploadModal = ({
  isModalOpen,
  setIsModalOpen,
  refreshAllComponents,
}) => {
  const [selectedFile, setSelectedFile] = useState(null);

  const handleSave = () => {
    if (!!selectedFile) {
      TestCaseAPI.bulkUpload(selectedFile)
        .then(() => {
          refreshAllComponents();
          notification.success({
            placement: "top",
            message:"Testcase configuration has been updated successfully",
          });
        })
        .catch((error) => {});
    }
    setSelectedFile(null);
    setIsModalOpen(false);
  };

  const handleFileRemove = () => {
    setSelectedFile(null);
  };

  const handleCancel = () => {
    setIsModalOpen(false);
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const renderTooltip = (props) => (
    <Tooltip {...props}>
      Accepted file types: CSV and XLSX only.
      <br /> Maximum file size: 10MB.
    </Tooltip>
  );

  return (
    <div>
      <Modal
        open={isModalOpen}
        onCancel={handleCancel}
        footer={[
          <Button key="cancel" onClick={handleCancel}>
            Cancel
          </Button>,
          <Button
            key="save"
            type="primary"
            onClick={handleSave}
            disabled={!selectedFile}
          >
            Save
          </Button>,
        ]}
      >
        <p className="bulkUploadHeader">
          Bulk Upload Manual TestCase Configuration{" "}
          <OverlayTrigger
            placement="top"
            delay={{ show: 250, hide: 400 }}
            overlay={renderTooltip}
          >
            <i className="bi bi-info-circle-fill document-tooltip-info"></i>
          </OverlayTrigger>
        </p>

        {!selectedFile && (
          <div>
            <button
              variant="success"
              type="button"
              className="btn cst-btn-default-bulk-upload"
              onClick={() => document.getElementById("file-upload").click()}
            >
              <i
                className="bi bi-file-zip-fill"
                style={{ paddingRight: "3px" }}
              ></i>
              Upload File
            </button>
            <input
              id="file-upload"
              type="file"
              accept=".csv, .xlsx"
              style={{ display: "none" }}
              onChange={handleFileChange}
            />
          </div>
        )}

        {!!selectedFile && (
          <p className="BulkUploadModalSelectedFile">
            {" "}
            {selectedFile.name}{" "}
            <Button
              type="text"
              icon={<DeleteOutlined />}
              onClick={handleFileRemove}
            />
          </p>
        )}
      </Modal>
    </div>
  );
};

export default BulkUploadModal;