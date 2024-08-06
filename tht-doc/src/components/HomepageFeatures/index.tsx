import React from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Overview',
    description: (
      <>
        The THT Testing Application is designed to ensure that applications comply with OpenHIE standards, providing a robust framework for health information exchange. Here, you will find all the necessary tools and documentation to manage and monitor the application testing process.
      </>
    ),
  },
  {
    title: 'Test Case Management',
    description: (
      <>
        Configuring and modifying test cases is a key feature of this application. You can create and update test cases to meet specific testing requirements and ensure they align with the OpenHIE Architecture. This helps maintain the integrity and quality of the testing process.
      </>
    ),
  },
  {
    title: 'Getting Started',
    description: (
      <>
        To get started, explore the sections of this documentation to learn more about each feature and how to utilize the THT Testing Application effectively. Detailed guides and instructions are provided to help you navigate the application and make the most of its capabilities.
      </>
    ),
  },
];

function Feature({ title, description }: FeatureItem) {
  return (
    <div className={clsx('col col--4', styles.feature)}>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
