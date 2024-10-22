module.exports = {
  extends: ['react-app', 'react-app/jest', 'prettier'],
  plugins: ['prettier'],
  rules: {
    'no-unused-vars': 'warn',
    'react/jsx-uses-react': 'off',
    'react/react-in-jsx-scope': 'off',
    'react/no-unescaped-entities': 'warn',
    'prettier/prettier': 'error',
  },
};
