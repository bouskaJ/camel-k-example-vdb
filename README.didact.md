# Camel K and Data Virtualization Example

![Camel K CI](https://github.com/openshift-integration/camel-k-example-basic/workflows/Camel%20K%20CI/badge.svg)

This example demonstrates how to get started with Camel K and Data Virtualization, where user installs Camel-K and DV Operator, then using the DV Operator deploys a Virtual Database then connect virtual database using OData connector on Camel-K to read and write data into Virtual Database

## Before you begin

Make sure you check-out this repository from git and open it with [VSCode](https://code.visualstudio.com/).

Instructions are based on [VSCode Didact](https://github.com/redhat-developer/vscode-didact), so make sure it's installed
from the VSCode extensions marketplace.

From the VSCode UI, click on the `README.didact.md` file and select "Didact: Start Didact tutorial from File". A new Didact tab will be opened in VS Code.

[Make sure you've checked all the requirements](./requirements.didact.md) before jumping into the tutorial section.

## Checking requirements

<a href='didact://?commandId=vscode.didact.validateAllRequirements' title='Validate all requirements!'><button>Validate all Requirements at Once!</button></a>

**VS Code Extension Pack for Apache Camel**

The VS Code Extension Pack for Apache Camel by Red Hat provides a collection of useful tools for Apache Camel K developers,
such as code completion and integrated lifecycle management.

You can install it from the VS Code Extensions marketplace.

[Check if the VS Code Extension Pack for Apache Camel by Red Hat is installed](didact://?commandId=vscode.didact.extensionRequirementCheck&text=extension-requirement-status$$redhat.apache-camel-extension-pack&completion=Camel%20extension%20pack%20is%20available%20on%20this%20system. "Checks the VS Code workspace to make sure the extension pack is installed"){.didact}

_Status: unknown_{#extension-requirement-status}

**OpenShift CLI ("oc")**

The OpenShift CLI tool ("oc") will be used to interact with the OpenShift cluster.

[Check if the OpenShift CLI ("oc") is installed](didact://?commandId=vscode.didact.requirementCheck&text=oc-requirements-status$$oc%20version$$oc&completion=OpenShift%20%20CLI%20is%20available%20on%20this%20system. "Tests to see if `oc version` returns a result"){.didact}

_Status: unknown_{#oc-requirements-status}

**Connection to an OpenShift cluster**

You need to connect to an OpenShift cluster in order to run the examples.

[Check if you're connected to an OpenShift cluster](didact://?commandId=vscode.didact.requirementCheck&text=cluster-requirements-status$$oc%20get%20project$$NAME&completion=OpenShift%20is%20connected. "Tests to see if `kamel version` returns a result"){.didact}

_Status: unknown_{#cluster-requirements-status}

**Apache Camel K CLI ("kamel")**

Apart from the support provided by the VS Code extension, you also need the Apache Camel K CLI ("kamel") in order to
access all Camel K features.

[Check if the Apache Camel K CLI ("kamel") is installed](didact://?commandId=vscode.didact.requirementCheck&text=kamel-requirements-status$$kamel%20version$$Camel%20K%20Client&completion=Apache%20Camel%20K%20CLI%20is%20available%20on%20this%20system. "Tests to see if `kamel version` returns a result"){.didact}

_Status: unknown_{#kamel-requirements-status}

## 1. Preparing a new OpenShift project

We'll setup a new project called `camel-vdb` where we'll run the integrations.

To create the project, open a terminal tab and type the following command:

```
oc new-project camel-vdb
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20new-project%20camel-vdb&completion=New%20project%20creation. "Opens a new terminal and sends the command above"){.didact})

Upon successful creation, you should ensure that the Camel K operator is installed. We'll use the `kamel` CLI to do it:

```
kamel install
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20install&completion=Camel%20K%20operator%20installation. "Opens a new terminal and sends the command above"){.didact})

Camel K should have created an IntegrationPlatform custom resource in your project. To verify it:

```
oc get integrationplatform
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20integrationplatform&completion=Camel%20K%20integration%20platform%20verification. "Opens a new terminal and sends the command above"){.didact})

If everything is ok, you should see an IntegrationPlatform named `camel-k` with phase `Ready`.

**DV (Teiid) Operator**

Apart from the support provided by the VS Code extension, and "kamel" you also need DV (Teiid) Operator in order to deploy a Virtual Database. We will use script to install it.

```
./dvinstall.sh
```

When you execute this script, you need to provide your credentials for RedHat customer portal to access the Operator image. Follow the command prompts below

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$.%2Fdvinstall.sh%0A&completion=DV%20K%20operator%installation. "Opens a new terminal and sends the command above"){.didact})

Now lets verify that the teiid-operator is installed correctly

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20pods%20--selector%20name%3Dteiid-operator&completion=DV%20K%20verification. "Opens a new terminal and sends the command `oc get pods --selector name=teiid-operator`"){.didact})

If everything is ok, you should see an DV or Teiid Operator pod below in terminal.

## 2. Deploy a Virtual Database

For the purposes of this example lets deploy a Virtual Database, that is built on top of in memory based H2 database for simplicity. This Virtual database will exposes a single table called `NOTE`, then DV will expose a OData API on it.

```
oc create -f dv-greeting.yaml
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%create%20-f%20dv-greeting.yaml&completion=DV%20VDB%20deploy%20verification. "Opens a new terminal and sends the command `oc create -f dv-greeting.yaml`"){.didact})

This will take sometime to deploy, dpending upon you this first time you are deploying a VDB. Anywhere between 3-4 minutes, once done the Virtual Database `dv-greeting` should be deployed ready to query. Now let's check if the Virtual Database is available

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20vdb%20dv-greeting%20-o%20yaml&completion=DV%20K%20verification. "Opens a new terminal and sends the command `oc get vdb dv-greeting -o yaml`"){.didact})

Now make sure the Status is `Running` for the Virtual Database.

## 2. Running a VDB integration

This repository contains a simple Camel K integration that periodically reads and writes from the Virtual Database that is deployed above

The integration is all contained in a single file named `Vdb.java` ([open](didact://?commandId=vscode.openFolder&projectFilePath=Vdb.java&completion=Opened%20the%20Vdb.java%20file "Opens the Vdb.java file"){.didact}).

> **Note:** the `Vdb.java` file contains a simple integration that uses the `timer` and `odata` components.
> Dependency management is automatically handled by Camel K that imports all required libraries from the Camel
> catalog via code inspection. This means you can use all 300+ Camel components directly in your routes.

We're ready to run the integration on our `camel-vdb` project in the cluster.

Use the following command to run it in "dev mode", in order to see the logs in the integration terminal:

```
kamel run Vdb.java --dev
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20Vdb.java%20--dev&completion=Camel%20K%20vdb%20integration%20run%20in%20dev%20mode. "Opens a new terminal and sends the command above"){.didact})

If everything is ok, after the build phase finishes, you should see the Camel integration running and continuously printing "Hello World!..." in the terminal window.

When running in dev mode, you can change the integration code and let Camel K redeploy the changes automatically.

**To exit dev mode and terminate the execution**, click on the terminal window and press `ctrl+c`.

> **Note:** When you terminate a "dev mode" execution, also the remote integration will be deleted. This gives the experience of a local program execution, but the integration is actually running in the remote cluster.

To keep the integration running and not linked to the terminal, you can run it without "dev mode", just run:

```
kamel run Vdb.java
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20Vdb.java&completion=Camel%20K%20vdb%20integration%20run. "Opens a new terminal and sends the command above"){.didact})

After executing the command, you should be able to see it among running integrations:

```
oc get integrations
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20integrations&completion=Getting%20running%20integrations. "Opens a new terminal and sends the command above"){.didact})

An integration named `Vdb` should be present in the list and it should be in status `Running`. There's also a `kamel get` command which is an alternative way to list all running integrations.
