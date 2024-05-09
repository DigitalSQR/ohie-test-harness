import React from 'react';
import applicationsIcon from '../../../../styles/images/applications-icon.png';
import assesseIcon from '../../../../styles/images/assesse-icon.png';
import complianceIcon from '../../../../styles/images/compliance-icon.png';
import testingIcon from '../../../../styles/images/testing-icon.png';

const Statistics = (props) => {
  // Determine which icon to use based on the parameter
  let iconPath;
  switch (props.parameter) {
    case 'Applications':
      iconPath = applicationsIcon;
      break;
    case 'Assessees Registered':
      iconPath = assesseIcon;
      break;
    case 'Compliance Rate':
      iconPath = complianceIcon;
      break;
    case 'Testing Rate':
      iconPath = testingIcon;
      break;
    default:
      iconPath = null;
  }

  return (
    <div className="container"  
      style={{
        paddingLeft: props.index === 0 ? '0' : '',
        paddingRight: props.index === (props.array.length - 1) ? '0' : ''
      }}
    >
      <div className="d-flex justify-content-between align-items-center p-3 border rounded card flex-row">
        <div className='text-start'>
          <p className="mb-2 ">{props.parameter}</p>
          <p className="m-0 fs-3 fw-bold ">{props.value}</p>
        </div>
        {iconPath && <img src={iconPath} alt={props.parameter} style={{ width: '40px', height: '40px' }} />}
      </div>
    </div>
  );
}

export default Statistics;