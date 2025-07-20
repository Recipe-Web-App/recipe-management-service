function fn() {
  var env = karate.env || 'minikube';
  var config = {
    baseUrl: 'http://localhost:8080'
  };

  if (env === 'local') {
    config.baseUrl = 'http://localhost:8080';
  } else if (env === 'minikube') {
    config.baseUrl = 'http://recipe-manager.local';
  }

  return config;
}
