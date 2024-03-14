import React, { Fragment, useEffect, useState } from "react";
import { useFormik } from "formik";
import "./userProfile.scss";
import { useLoader } from "../../loader/LoaderContext";
import { UserAPI } from "../../../api/UserAPI";
import { useNavigate } from "react-router-dom";
import { Upload, notification, Modal } from "antd";
import ImgCrop from "antd-img-crop";
import { userinfo_success } from "../../../reducers/UserInfoReducer";
import { useDispatch, useSelector } from "react-redux";
import { getHighestPriorityRole } from "../../../utils/utils";
import { DocumentAPI } from "../../../api/DocumentAPI";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import {
  DOCUMENT_STATE_ACTIVE,
  DOCUMENT_STATE_INACTIVE,
  DOCUMENT_TYPE_FOR_USER,
} from "../../../constants/document_constants";
import avatar from "../../../styles/images/defaultDP.jpeg";
import { set_header } from "../../../reducers/homeReducer";
import UseEventEmitter from "../../CommonFiles/EventEmitter/EventEmitter";
import { APP_EVENTS } from "../../../constants/event_constants";

/**
 * UserProfile Component:
 * Displays the user profile information and provides functionality to update the user details and profile picture.
 */
export default function UserProfile () {

  // Function to display the loader.
  const { showLoader, hideLoader } = useLoader();

  // State to store the user details.
  const [userDetails, setUserDetails] = useState();

  // State to control the visibility of the image preview modal.
  const [previewOpen, setPreviewOpen] = useState(false);

  // State to store the URL of the image to be previewed.
  const [previewImage, setPreviewImage] = useState("");

  // State to store the title of the image preview modal.
  const [previewTitle, setPreviewTitle] = useState("");

  // State to track if a new image has been uploaded.
  const [uploadedFlag, setUploadedFlag] = useState(false);

  // State to store the previous profile picture.
  const [previousImage, setPreviousImage] = useState();

  // State to store the current profile picture.
  const [profilePicture, setProfilePicture] = useState();

  // Function to navigate to different routes.
  const navigate = useNavigate();

  // Function to dispatch actions to the Redux store.
  const dispatch = useDispatch();

  // Selector to retrieve the user ID from the Redux store.
  const userID = useSelector((store) => store.userInfoSlice.id);

  // Function to handle profile picture upload
  const handleUpload = () => {

    // If there is a previous profile picture and the picture has deleted.
    if (previousImage && profilePicture === undefined) {

      // Change the state of the previous image document to inactive
      DocumentAPI.changeDocumentState(
        previousImage.documentId,
        DOCUMENT_STATE_INACTIVE
      ).then((res)=>{        
        // Refresh the user profile after successful document state change
        refreshUserProfile();
      }).catch((error) => {
        // Handle the error here
        hideLoader();
      });
    }else if (profilePicture && profilePicture !== previousImage) {
      const formData = new FormData();
      formData.append(`file`, profilePicture.file);
      formData.append(`fileName`, profilePicture.file.name);
      formData.append(`refId`, userID);
      formData.append(`refObjUri`, RefObjUriConstants.USER_REFOBJURI);
      formData.append(`documentType`, DOCUMENT_TYPE_FOR_USER.PROFILE_PICTURE);
      return DocumentAPI.uploadDocument(formData)
        .then(async (response) => {
          const base64Image = await DocumentAPI.base64Document(
            response.id,
            response.name
          );
          let imageData = {
            name: response.name,
            status: "done",
            url: base64Image,
            documentId: response.id,
          };
          setPreviewImage(imageData);
          refreshUserProfile();
          
        })
        .catch((error) => {
          hideLoader();
        });
    }else {
       refreshUserProfile();
    }
  };

  // Function to validate form input values.
  const validate = (values) => {
    const errors = {};
    if (values?.name?.length === 0) {
      errors.name = "Please enter your name.";
    } else if (values?.name?.length > 1000) {
      errors.password = "Password must have less than 1000 characters.";
    }
    if (values?.companyName?.length == 0) {
      errors.companyName = "Please enter your company's name.";
    } else if (values?.companyName?.length > 255) {
      errors.companyName = "Company name must have less than 255 characters.";
    }
    return errors;
  };

  // useEffect hook used to update the header title, fetch user data, and display profile picture on component mount.
  useEffect(() => {
    dispatch(set_header("User Profile"));
    fetchUser();
    fetchDisplayPicture();
  }, []);

  // Function to fetch and display the user's profile picture.
  const fetchDisplayPicture = () => {
    DocumentAPI.getDocumentsByRefObjUriAndRefId(
      RefObjUriConstants.USER_REFOBJURI,
      userID,
      DOCUMENT_STATE_ACTIVE
    ).then(async (res) => {
      if (res.content.length !== 0) {
        const name = res.content[0].name;
        const id = res.content[0].id;

        const base64Image = await DocumentAPI.base64Document(id, name);
        const image = {
          name: name,
          status: "done",
          url: base64Image,
          documentId: id,
        };
        setPreviousImage(image);
        setProfilePicture(image);
      }
    });
  };

  // Function to fetch user details from the API.
  const fetchUser = () => {
    UserAPI.viewUser()
      .then((res) => {
        setUserDetails(res);
        formik.values.name = res.name;
        formik.values.companyName = res.companyName;
        formik.values.email = res.email;
        formik.values.roleIds = getHighestPriorityRole(res);
      })
      .catch((error) => {});
  };

  // Function to handle the removal of the profile picture.
  const handleRemove = () => {
    Modal.confirm({
      title: "Delete Image",
      content: "Are you sure about deleting this image ?",
      okText: "Yes",
      cancelText: "Cancel",
      onOk() {       
        setProfilePicture();
        setUploadedFlag(true);
      },
    });
  };

  const handlePreview = () => {
    setPreviewImage(profilePicture.url);
    setPreviewOpen(true);

    return false;
  };

  const handleCancel = () => setPreviewOpen(false);

  // Formik hook to manage form state, validation, and submission.
  const formik = useFormik({
    initialValues: {
      name: "",
      email: "",
      roleIds: [],
      companyName: "",
    },
    validate: validate,
    onSubmit: (values) => {
      showLoader();
      const body = {
        ...userDetails,
        name: values.name,
        companyName: values.companyName,
      };
      UserAPI.UpdateExistingUser(body)
        .then((response) => {
          notification.success({
            placement: "bottomRight",
            description: `User Updated Successfully`,
          });
          dispatch(userinfo_success(body));
          handleUpload();
          hideLoader();          
          navigate(-1);
        })
        .catch((response) => {
          hideLoader();
        });
     
    },
  });

  // Function to handle the selection of a new profile picture.
  const handlePictureSelect = (e) => {
    setUploadedFlag(true);
    setProfilePicture(() => {
      const currentImage = {
        file: e.file,
        name: e.file.name,
        url: URL.createObjectURL(e.file),
      };
      return currentImage;
    });
  };

  // Function to emit an event to refresh the user profile.
  const refreshUserProfile = () => {
    UseEventEmitter.emit(APP_EVENTS.REFRESH_USER_PROFILE, "");
  };

  return (
    <div id="userProfile">
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">User Profile</span>
            <div className="row" style={{ alignItems: "center" }}>
              <div
                className="col-12"
                style={{
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <div
                  style={{
                    position: "relative",
                    width: "25%",
                    height: "25%",
                    overflow: "hidden",
                    marginBottom: "8px",
                    borderRadius: "2rem",
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.lastChild.style.opacity = 1;
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.lastChild.style.opacity = 0;
                  }}
                >
                  <img
                    src={profilePicture ? profilePicture.url : avatar}
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "contain",
                      borderRadius: "2rem",
                    }}
                  />
                  <div
                    style={{
                      position: "absolute",
                      top: 0,
                      left: 0,
                      width: "100%",
                      height: "100%",
                      backgroundColor: "rgba(0, 0, 0, 0.5)",
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                      opacity: 0,
                      transition: "opacity 0.3s",
                    }}
                  >
                    {profilePicture && (
                      <Fragment>
                        <span
                          onClick={handlePreview}
                          className="bi bi-eye-fill"
                          style={{
                            fontSize: "2em",
                            color: "white",
                            marginRight: "10px",
                            cursor: "pointer"
                          }}
                        />

                        <span
                          onClick={handleRemove}
                          className="bi bi-trash-fill"
                          style={{ fontSize: "2em", color: "white",cursor: "pointer" }}
                        />
                      </Fragment>
                    )}
                  </div>
                </div>
                {!profilePicture && (
                  <ImgCrop>
                    <Upload
                      type="button"
                      showUploadList={false}
                      maxCount={1}
                      customRequest={(e) => handlePictureSelect(e)}
                      previewFile={false}
                      accept=".png,.jpg,.jpeg,image/png,image/jpeg"
                    >
                      <button
                        type="button"
                        className="btn btn-primary btn-sm btn-blue"
                        style={{ marginTop: 8 }}
                      >
                        Upload
                      </button>
                    </Upload>
                  </ImgCrop>
                )}
              </div>
              <Modal
                open={previewOpen}
                title={previewTitle}
                footer={null}
                onCancel={handleCancel}
              >
                <img
                  alt="example"
                  style={{
                    width: "100%",
                  }}
                  src={previewImage}
                />
              </Modal>
            </div>

            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="name" className="form-label">
                    <b>Name</b>
                    <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="name"
                    placeholder="Your Name"
                    name="name"
                    value={formik.values.name}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                  />
                  {formik.touched.name && formik.errors.name && (
                    <div className="text-danger">{formik.errors.name}</div>
                  )}
                </div>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="companyName" className="form-label">
                    <b>Company Name</b>
                    <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="companyName"
                    placeholder="Company Name"
                    name="companyName"
                    value={formik.values.companyName}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                  />
                  {formik.touched.companyName && formik.errors.companyName && (
                    <div className="text-danger">
                      {formik.errors.companyName}
                    </div>
                  )}
                </div>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="roleIds" className="form-label">
                    <b>Role</b>
                    <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    name="roleIds"
                    id="roleIds"
                    value={formik.values.roleIds}
                    className="form-control disable-field"
                    disabled
                  ></input>
                </div>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="email" className="form-label">
                    <b>Email</b>
                    <span className="text-danger">*</span>
                  </label>
                  <input
                    type="email"
                    className="form-control disable-field"
                    id="email"
                    placeholder="Your Email"
                    name="email"
                    disabled
                    value={formik.values.email}
                  />
                </div>
              </div>
            </div>

            {/* <div className="my-4 text-end"> */}
            <div
              className="my-4"
              style={{ display: "flex", justifyContent: "flex-end" }}
            >
              <button
                className="btn btn-primary btn-white font-size-11 reset-button"
                style={{ marginRight: "auto" }}
                onClick={() => {
                  navigate("/reset-password");
                  formik.resetForm();
                }}
              >
                Reset Password
              </button>
              <button
                className="btn btn-primary btn-white font-size-14"
                onClick={() => {
                  navigate(-1);
                  formik.resetForm();
                }}
              >
                Cancel
              </button>
              <button
                disabled={!((formik.isValid && formik.dirty) || uploadedFlag)}
                onClick={formik.handleSubmit}
                className="btn btn-primary btn-blue btn-submit  font-size-14"
                style={{ marginLeft: "1rem" }}
                type="submit"
              >
                Update
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
