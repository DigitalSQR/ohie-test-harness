import { Fragment, useEffect, useState } from "react";
import { TestCaseAPI } from "../../../../../api/TestCaseAPI";
import { Modal, notification } from "antd";
import { ErrorMessage, Field, Form, Formik, setIn } from "formik";
import * as Yup from "yup";
import "./SpecAutomatedUpsertModal.scss";
import { useLoader } from "../../../../loader/LoaderContext";

export default function SpecAutomatedUpsertModal(props) {
  const {
    isAutomatedModalOpen,
    setIsAutomatedModalOpen,
    specificationId,
    isEditMode,
    setIsEditMode,
    currentAutomatedTestcase,
    fetchAutomatedTestCases,
  } = props;

  const [initialValues, setInitialValue] = useState({
    name: "",
    description: ""
  });

  const { showLoader, hideLoader } = useLoader();


  const [uploadedZipFile, setUploadedZipFile] = useState();
  const [currentTestCaseId,setCurrentTestCaseId] = useState();
  const handleCancel = () => {
    setInitialValue({ name: "", description: ""});
    setUploadedZipFile();
    setIsAutomatedModalOpen(false);
    setIsEditMode(false);
    setCurrentTestCaseId();
  };

  const handleSubmit = (values) => {
  
    const data = {
      // zipFile: uploadedZipFile,
      name: values.name,
      specificationId: specificationId,
      description: "",
      manual: false,
      testcaseRunEnvironment: "testcase.run.environment.eu.testbed",
      zipFile:uploadedZipFile
    };


    if(isEditMode){
      const data = currentAutomatedTestcase;
      data.name = values.name;
      data.zipFile = uploadedZipFile;
      showLoader();
      TestCaseAPI.updateTestCase(data).then((res)=>{
        notification.success({
          className:"notificationSuccess",
          message: "Automated TestCase Updated successfully",
          placement: "top",
        });
        setIsAutomatedModalOpen(false);
        setIsEditMode(false);
        setInitialValue({
          name: "",
          description: "",
        });
        fetchAutomatedTestCases();
        setCurrentTestCaseId();
      }).catch(()=>{})
      hideLoader();
    }else{
      showLoader();
    TestCaseAPI.createTestCase(data)
      .then((res) => {
        notification.success({
          className:"notificationSuccess",
          message: "Automated TestCase Sucessfully created",
          placement: "top",
        });
        setIsAutomatedModalOpen(false);
        setIsEditMode(false);
        setInitialValue({
          name: "",
          description: "",
        });
        fetchAutomatedTestCases();
      })
      .catch(() => {});
      hideLoader();

    }
  };

  const validationSchema = Yup.object({
    name: Yup.string()
      .trim()
      .required("Name is required *")
      .min(3, "Name must be of minimum 3 characters")
      .max(1000, "Name must be of maximum 1000 characters"),
  });

  useEffect(() => {
    console.log(currentAutomatedTestcase);
    if (currentAutomatedTestcase) {
      setCurrentTestCaseId(currentAutomatedTestcase.id);
      setInitialValue({
        name: currentAutomatedTestcase.name,
        description: currentAutomatedTestcase.description
      });
      setUploadedZipFile();
    } else {
      setInitialValue({
        name: "",
        description: ""
      });
      setUploadedZipFile();
      setCurrentTestCaseId();
    }

  
  }, [isAutomatedModalOpen, currentAutomatedTestcase]);

  return (
    <Fragment>
      <Modal
        open={isAutomatedModalOpen}
        onCancel={handleCancel}
        destroyOnClose={true}
        footer={null}
      >
        <h5>{isEditMode ? "Update Automated TestCase" : "Create Automated TestCase"}</h5>
        <Formik
          enableReinitialize={true}
          initialValues={initialValues}
          onSubmit={handleSubmit}
          validationSchema={validationSchema}
        >
          {({ isValid, dirty }) => (
            <Form>
              <div className="row">
                <div className="col-12">
                  <div className="custom-input mb-3">
                    <label htmlFor="name" className="form-label">
                      Name
                    </label>
                    <Field
                      type="text"
                      id="name"
                      name="name"
                      className="form-control"
                      placeholder="Name"
                    />
                    <ErrorMessage
                      name="name"
                      component="div"
                      className="error-message"
                    />
                  </div>
                  {!!uploadedZipFile && (
                    <div>
                      {uploadedZipFile.name}
                      <span
                        type="button"
                        title="Remove File"
                        className="mx-2 font-size-14"
                        onClick={() => setUploadedZipFile()}
                      >
                        <i className="bi bi-trash3"></i>
                      </span>
                    </div>
                  )}
                  {!uploadedZipFile && (
                    <div
                      className="cst-btn-group btn-group margin"
                      role="group"
                      aria-label="Basic example"
                    >
                      <input
                        type="file"
                        style={{ display: "none" }}
                        id="fileInput"
                        onChange={(e) => {
                          console.log(e.target.files);
                          setUploadedZipFile(e.target.files[0]);
                        }}
                      ></input>
                      <label htmlFor="fileInput">
                        <button
                          variant="success"
                          type="button"
                          className="btn cst-btn-default"
                          onClick={() =>
                            document.getElementById("fileInput").click()
                          }
                        >
                          <i
                            class="bi bi-file-zip-fill"
                            style={{ paddingRight: "3px" }}
                          ></i>{" "}
                          {isEditMode? "Update Zip Fie":"Upload Zip File"}
                        </button>
                      </label>
                    </div>
                  )}
                  <div className="my-4 text-end">
                    <button
                      type="button"
                      className="btn btn-primary btn-white py-1 font-size-10 mx-3"
                      onClick={handleCancel}
                    >
                      Cancel
                    </button>

                    <button
                      type="submit"
                      className="btn btn-primary btn-blue btn-submit py-1 font-size-10"
                      disabled={(isEditMode && ((!isValid || !dirty) && !uploadedZipFile)) || (!isEditMode && (!isValid || !dirty || !uploadedZipFile))}
                      >
                      {isEditMode ? "Update" : "Submit"}
                    </button>
                  </div>
                </div>
              </div>
            </Form>
          )}
        </Formik>
      </Modal>
    </Fragment>
  );
}
