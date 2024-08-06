import React, { useState } from 'react';
import Lightbox from 'react-image-lightbox';
import 'react-image-lightbox/style.css';

const ImageViewer = ({ src }) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div>
      <img 
        src={src} 
        alt="click to view" 
        style={{ cursor: 'pointer' }} 
        onClick={() => setIsOpen(true)} 
      />
      {isOpen && (
        <Lightbox
          mainSrc={src}
          onCloseRequest={() => setIsOpen(false)}
        />
      )}
    </div>
  );
};

export default ImageViewer;
