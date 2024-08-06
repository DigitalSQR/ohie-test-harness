import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

const sidebars: SidebarsConfig = {
  
  tutorialSidebar: [
    {
      type: 'doc',
      label: 'Introduction',
      id: 'Introduction'
    },
    {
      type: 'category',
      label: 'Getting Started',
      items: ['Getting-Started/installation']
    },
    {
      type: 'category',
      label : 'User Guide',
      items : ['User-Guide/introduction',
      {
        type: 'category',
        label: 'Sign-Up, Login & Forgot Password',
        items: [
          'User-Guide/Authentication/Sign-up-process',
          'User-Guide/Authentication/Login-process',
          'User-Guide/Authentication/Forgot-password-process',
        ]
      },
      {
        type: 'category',
        label: 'Assessee Usage',
        items: ['User-Guide/Assessee-Usage/Apply-for-new-Testing-Request',
        'User-Guide/Assessee-Usage/report'
      ]
      },
      {
        type: 'category',
        label: 'Admin Usage',
        items: ['User-Guide/Admin-Usage/Assessee',
        'User-Guide/Admin-Usage/User-creation',
        'User-Guide/Admin-Usage/Applications-and-Testing-Process',
        'User-Guide/Admin-Usage/Report',
      ]
      },
      'User-Guide/Publish-Usage'
    ]
    },
    {
      type: 'category',
      label: 'Developer Guide',
      items: ['Developer-Guide/Architecture-Overview',
      'Developer-Guide/Technology-Stack',
      {
        type: 'category',
        label: 'Layers',
        items: ['Developer-Guide/Layers/Frontend',
        'Developer-Guide/Layers/Authentication',
        'Developer-Guide/Layers/Buisness-Logic-Tier',
        'Developer-Guide/Layers/Data'
      ] 
      },
      'Developer-Guide/ERD',
      'Developer-Guide/Security',
      'Developer-Guide/Code-Quality'
    ]
    }
  ],
};

export default sidebars;
