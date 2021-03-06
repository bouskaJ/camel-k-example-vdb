# Camel K and Data Virtualization Example

This example demonstrates how to get started with Camel K and Data Virtualization, where user installs Camel-K and DV Operator, then using the DV Operator deploys a Virtual Database then connect virtual database using OData connector on Camel-K to read and write data into Virtual Database

## Before you begin

Make sure you check-out this repository from git and open it with [VSCode](https://code.visualstudio.com/).

Instructions are based on [VSCode Didact](https://github.com/redhat-developer/vscode-didact), so make sure it's installed
from the VSCode extensions marketplace.

From the VSCode UI, right-click on the `readme.didact.md` file and select "Didact: Start Didact tutorial from File". A new Didact tab will be opened in VS Code.

Make sure you've opened this readme file with Didact before jumping to the next section.

## Preparing the cluster

This example can be run on any OpenShift 4.3+ cluster or a local development instance (such as [CRC](https://github.com/code-ready/crc)). Ensure that you have a cluster available and login to it using the OpenShift `oc` command line tool.

You need to create a new project named `camel-vdb` for running this example. This can be done directly from the OpenShift web console or by executing the command `oc new-project camel-vdb` on a terminal window.

You need to install the Camel K operator in the `camel-vdb` project. To do so, go to the OpenShift 4.x web console, login with a cluster admin account and use the OperatorHub menu item on the left to find and install **"Red Hat Integration - Camel K"**. You will be given the option to install it globally on the cluster or on a specific namespace.
If using a specific namespace, make sure you select the `camel-vdb` project from the dropdown list.
This completes the installation of the Camel K operator (it may take a couple of minutes).

When the operator is installed, from the OpenShift Help menu ("?") at the top of the WebConsole, you can access the "Command Line Tools" page, where you can download the **"kamel"** CLI, that is required for running this example. The CLI must be installed in your system path.

Refer to the **"Red Hat Integration - Camel K"** documentation for a more detailed explanation of the installation steps for the operator and the CLI.

### Installing the DV (Teiid) Operator

In order to deploy a Virtual Database you also need the Data Virtualization (Teiid) Operator. This operator needs to be installed from the OperatorHub.

Now, go to your OpenShift 4.x WebConsole page, and find the OperatorHub menu item on left hand side menu and find "Data Virtualization Operator". Follow instructions (eg. setting correct secret) in operator description which are needed before clicking "Install" button. This may take a couple minutes to install.

You can use the following section to check if your environment is configured properly.

## Requirements

<a href='didact://?commandId=vscode.didact.validateAllRequirements' title='Validate all requirements!'><button>Validate all Requirements at Once!</button></a>

**OpenShift CLI ("oc")**

The OpenShift CLI tool ("oc") will be used to interact with the OpenShift cluster.

[Check if the OpenShift CLI ("oc") is installed](didact://?commandId=vscode.didact.cliCommandSuccessful&text=oc-requirements-status$$oc%20help&completion=Checking%20Openshift%20CLI%20tool%20is%20installed. "Tests to see if `oc help` returns a 0 return code"){.didact}

_Status: unknown_{#oc-requirements-status}

**Connection to an OpenShift cluster**

You need to connect to an OpenShift cluster in order to run the examples.

[Check if you're connected to an OpenShift cluster](didact://?commandId=vscode.didact.requirementCheck&text=cluster-requirements-status$$oc%20get%20project$$NAME&completion=Checking%20OpenShift%20is%20connected. "Tests to see if `kamel version` returns a result"){.didact}

_Status: unknown_{#cluster-requirements-status}

**Apache Camel K CLI ("kamel")**

Apart from the support provided by the VS Code extension, you also need the Apache Camel K CLI ("kamel") in order to
access all Camel K features.

[Check if the Apache Camel K CLI ("kamel") is installed](didact://?commandId=vscode.didact.requirementCheck&text=kamel-requirements-status$$kamel%20version$$Camel%20K%20Client&completion=Checking%20Apache%20Camel%20K%20CLI%20is%20available%20on%20this%20system. "Tests to see if `kamel version` returns a result"){.didact}

_Status: unknown_{#kamel-requirements-status}

### Optional Requirements

The following requirements are optional. They don't prevent the execution of the demo, but may make it easier to follow.

**VS Code Extension Pack for Apache Camel**

The VS Code Extension Pack for Apache Camel by Red Hat provides a collection of useful tools for Apache Camel K developers,
such as code completion and integrated lifecycle management. They are **recommended** for the tutorial, but they are **not**
required.

You can install it from the VS Code Extensions marketplace.

[Check if the VS Code Extension Pack for Apache Camel by Red Hat is installed](didact://?commandId=vscode.didact.extensionRequirementCheck&text=extension-requirement-status$$redhat.apache-camel-extension-pack&completion=Camel%20extension%20pack%20is%20available%20on%20this%20system. "Checks the VS Code workspace to make sure the extension pack is installed"){.didact}

*Status: unknown*{#extension-requirement-status}


## 1. Preparing the project

We'll connect to the `camel-vdb` project and check the installation status.

To change project, open a terminal tab and type the following command:


```
oc project camel-vdb
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20project%20camel-vdb&completion=New%20project%20creation. "Opens a new terminal and sends the command above"){.didact})


We should now check that the operator is installed. To do so, execute the following command on a terminal:

```
oc get csv
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20csv&completion=Checking%20Cluster%20Service%20Versions. "Opens a new terminal and sends the command above"){.didact})

When Camel K is installed, you should find an entry related to `red-hat-camel-k-operator` in phase `Succeeded`.

Now lets verify that also the dv-operator is installed correctly

```
oc get pods --selector name=dv-operator
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20pods%20--selector%20name%3Ddv-operator&completion=DV%20verification. "Opens a new terminal and sends the command `oc get pods --selector name=dv-operator`"){.didact})

If everything is ok, you should see an Data Virtualization Operator pod below in terminal.

## 2. Deploy a Virtual Database

For the purposes of this example lets deploy a Virtual Database, that is built on top of in memory based H2 database for simplicity. This Virtual database will exposes a single table called `NOTE`, then DV will expose a OData API on it.

```
oc create -f dv-dispatch.yaml
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20create%20-f%20dv-dispatch.yaml&completion=DV%20VDB%20deploy%20verification. "Opens a new terminal and sends the command `oc create -f dv-dispatch.yaml`"){.didact})

This will take sometime to deploy, especially if it's the first time a VDB is being deployed. After anywhere between 3-4 minutes the Virtual Database `dv-dispatch` should be deployed, ready for queries. Now let's check if the Virtual Database is available.

Make sure the Status is `Running` for the Virtual Database.

```
oc get vdb dv-dispatch -o yaml
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20vdb%20dv-dispatch%20-o%20yaml&completion=DV%20K%20verification. "Opens a new terminal and sends the command `oc get vdb dv-dispatch -o yaml`"){.didact})

## 3. Running a VDB integration

This repository contains a simple Camel K integration that periodically reads and writes from the Virtual Database that is deployed above.

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

If everything is ok, after the build phase finishes, you should see the Camel integration running and periodically printing dispatch messages in the terminal window.

When running in dev mode, you can change the integration code and let Camel K redeploy the changes automatically.

[**To exit dev mode and terminate the execution**, just click here](didact://?commandId=vscode.didact.sendNamedTerminalCtrlC&text=camelTerm&completion=Camel%20K%20basic%20integration%20interrupted. "Interrupt the current operation on the terminal"){.didact} 
or hit `ctrl+c` on the terminal window.

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

An integration named `vdb` should be present in the list and it should be in status `Running`. There's also a `kamel get` command which is an alternative way to list all running integrations.

## 4. Uninstall

To cleanup everything, execute the following command:

```oc delete project camel-vdb```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20delete%20project%20camel-vdb&completion=Removed%20the%20project%20from%20the%20cluster. "Cleans up the cluster after running the example"){.didact})
