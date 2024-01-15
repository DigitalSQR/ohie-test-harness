import React, { useEffect, useState } from "react";
import { ComponentAPI } from "../../../api/ComponentAPI";

const ComponentIdConnector = ({componentId}) => {
    const [component, setComponent] = useState();

    useEffect(() => {
        ComponentAPI.getComponentById(componentId).then((res) => {
            setComponent(res);
        });
    }, []);

    return (
        component?.name ?
        <div>
            {component.name}
        </div>
        : null 
    );
}

export default ComponentIdConnector;