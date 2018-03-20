# Deploy using kubernetes
## On MiniKube (local cluster)
- Install [MiniKube](https://github.com/kubernetes/minikube)

Check if kubecontrol and minikube etc are working (please check docs on their
website):
```
kubectl
minikube dashboard
```
Install mongodb with helm:
```
brew install kubernetes-helm
# enable tiller on cluster
helm init --service-account=tiller
# use genome nexus mongo db image
helm install --name gn-mongo --set image=genomenexus/gn-mongo:latest,persistence.size=500Mi stable/mongodb
```
For spring boot app use k8s yamls:
```
kubectl create -f .
```
For a simple rolling deploy, change the version number in `gn_spring_boot.yml`
and run (this will update to new version with zero downtime):
```
kubectl apply -f deploy/ && kubectl rollout status deploy gn-spring-boot
```
## On Amazon
- Make an S3 bucket for keeping track of K8s state (with name `NAME`)
- Install aws command line client
- Install [kops](https://github.com/kubernetes/kops) (creates kubernetes cluster on amazon)

Then:
```
export STATE_STORE=s3://random-k8s-state-store-name
export NAME=genome-nexus.review.k8s.local
# create config
kops create cluster --state=${STATE_STORE} --cloud=aws --zones=us-east-1a,us-east-1c --node-count=2   --node-size=t2.nano --master-size=t2.small ${NAME}
# create cluster
kops update cluster ${NAME} --yes --state ${STATE_STORE}
```
Then same things as above to deploy the apps.
