import React from "react";
import "../scss/_registrationApplication.scss"

const RegisterApplication = () => {
  return (
    <div>
      <div id="wrapper">
        <div class="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div class="form-bg-white">
            <span class="heading-line-up">Application Details</span>

            <div class="row">
              <div class="col-sm-6 col-12">
                <div class="custom-input mb-3">
                  <label for="exampleFormControlInput1" class="form-label">
                    Application Name
                  </label>
                  <input
                    type="text"
                    class="form-control"
                    placeholder="Application Name"
                  />
                </div>
              </div>
              <div class="col-sm-6 col-12">
                <div class="custom-input mb-3">
                  <label for="exampleFormControlInput1" class="form-label">
                    Application URL
                  </label>
                  <input
                    type="text"
                    class="form-control"
                    placeholder="Application URL"
                  />
                </div>
              </div>
            </div>

            <div class="row">
              <div class="col-12">
                <div class="custom-input mb-3">
                  <label for="exampleFormControlInput1" class="form-label">
                    Application Description
                  </label>
                  <textarea
                    class="form-control custom-textarea"
                    id="exampleFormControlTextarea1"
                    rows="3"
                    placeholder="Application Description"
                  ></textarea>
                </div>
              </div>
            </div>

            <div class="row">
              <div class="col-12">
                {" "}
                <label for="exampleFormControlInput1" class="form-label">
                  Credentials
                </label>
              </div>
              <div class="col-sm-6 col-12">
                <div class="custom-input mb-3">
                  <input
                    type="text"
                    class="form-control"
                    placeholder="Username"
                  />
                </div>
              </div>
              <div class="col-sm-6 col-12">
                <div class="custom-input mb-3">
                  <input
                    type="Password"
                    class="form-control"
                    placeholder="Password"
                  />
                </div>
              </div>
            </div>

            <div class="row">
              <div class="col-12">
                <div class="custom-input mb-3">
                  <label for="exampleFormControlInput1" class="form-label">
                    Select OpenHIE Components:{" "}
                    <i class="bi bi-info-circle-fill cursor-pointer"></i>
                  </label>
                  <select
                    class="form-select custom-select"
                    aria-label="Default select example"
                  >
                    <option selected>Component name</option>
                    <option value="1">One</option>
                    <option value="2">Two</option>
                    <option value="3">Three</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
          <div class="my-4 text-end">
            <button class="btn btn-primary btn-white py-2 font-size-14">
              Cancel
            </button>
            <button class="btn btn-primary btn-blue py-2 font-size-14">
              submit
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterApplication;
