import React from "react";
import sortIcon from "../styles/images/sort-icon.png";
import "../scss/_registrationRequest.scss"

const RegistrationRequest = () => {
  return (
    <div id="wrapper">
      <div class="col-12">
        {/* --filter-- */}
        <div class="row mb-2 justify-content-between">
          <div class="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12">
            <div class="custom-input custom-input-sm mb-3">
              <input type="text" class="form-control" placeholder="Search" />
            </div>
          </div>
          <div class="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
            <div class="d-flex align-items-baseline justify-content-end">
              <span class="pe-3 text-nowrap">Status :</span>
              <div class="mb-3">
                <select
                  class="form-select custom-select custom-select-sm"
                  aria-label="Default select example"
                >
                  <option selected="">Component name</option>
                  <option value="1">One</option>
                  <option value="2">Two</option>
                  <option value="3">Three</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        {/* --filter END-- */}

        <div class="table-responsive">
          <table class=" data-table">
            <thead>
              <tr>
                <th>
                  NAME{" "}
                  <a class="ps-1" href="#">
                    <img src={sortIcon} alt="e"/>
                  </a>
                </th>
                <th>
                  Email{" "}
                  <a class="ps-1" href="# ">
                    <img src={sortIcon} alt="e" />
                  </a>
                </th>
                <th>requested date</th>
                <th>
                  Company{" "}
                  <a class="ps-1" href="#v">
                    <img src={sortIcon} alt="e" />
                  </a>
                </th>
                <th>
                  Status{" "}
                  <a class="ps-1" href="# ">
                    <img src={sortIcon} alt="e" />
                  </a>
                </th>
                <th>CHOOSE ACTION</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>William Elmore</td>
                <td>William@gmail.com</td>
                <td>12 Mar 2023</td>
                <td>Unified Infotech</td>
                <td>
                  <span class="badges-green-dark">Active</span>
                </td>
                <td></td>
              </tr>
              <tr>
                <td>Georgie Winters</td>
                <td>Georgie@gmail.com</td>
                <td>12 Mar 2023</td>
                <td>ABC Solutions</td>
                <td>
                  <span class="badges-orange">Pending</span>
                </td>
                <td class=" no-wrap">
                  <span class="text-uppercase">
                    <i class="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
                    APPROVE{" "}
                  </span>
                  <span class="text-uppercase ps-3">
                    <i class="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
                    REJECT{" "}
                  </span>
                </td>
              </tr>
              <tr>
                <td>Whitney Meier</td>
                <td>Whitneymeier@gmail.com</td>
                <td>12 Mar 2023</td>
                <td>Taurus LLC</td>
                <td>
                  <span class="badges-orange">Pending</span>
                </td>
                <td class=" no-wrap">
                  <span class="text-uppercase">
                    <i class="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
                    APPROVE{" "}
                  </span>
                  <span class="text-uppercase ps-3">
                    <i class="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
                    REJECT{" "}
                  </span>
                </td>
              </tr>
              <tr>
                <td>Justin Maier</td>
                <td>Justinmaier@gmail.com</td>
                <td>12 Mar 2023</td>
                <td>Zerbia Inc.</td>
                <td>
                  <span class="badges-orange">Pending</span>
                </td>
                <td class=" no-wrap">
                  <span class="text-uppercase">
                    <i class="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
                    APPROVE{" "}
                  </span>
                  <span class="text-uppercase ps-3">
                    <i class="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
                    REJECT{" "}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default RegistrationRequest;
